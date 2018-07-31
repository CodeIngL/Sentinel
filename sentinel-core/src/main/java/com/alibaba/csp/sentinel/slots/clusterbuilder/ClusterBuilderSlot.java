package com.alibaba.csp.sentinel.slots.clusterbuilder;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.csp.sentinel.Env;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slotchain.StringResourceWrapper;

/**
 * <p>
 * This slot maintains resource running statistics (response time, qps, thread
 * count, exception), and a list of callers as well which is marked by
 * {@link ContextUtil#enter(String origin)}
 * </p>
 * <p>
 * One resource has only one cluster node, while one resource can have multiple
 * default node.
 * </p>
 * <p>
 * <p>
 * 此插槽维护resource运行统计信息（响应时间，qps，线程计数，异常）以及由{@link ContextUtil#enter(String origin)}标记的调用者列表
 * </p>
 * <p>
 * 一个resource只有一个cluster node，而一个resource可以有多个默认default node。
 * </p>
 *
 * @author jialiang.linjl
 */
public class ClusterBuilderSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    /**
     * <p>
     * Remember that same resource({@link ResourceWrapper#equals(Object)}) will share
     * the same {@link ProcessorSlotChain} globally, no matter in witch context. So if
     * code goes into {@link #entry(Context, ResourceWrapper, DefaultNode, int, Object...)},
     * the resource name must be same but context name may not.
     * </p>
     * <p>
     * To get total statistics of the same resource in different context, same resource
     * shares the same {@link ClusterNode} globally. All {@link ClusterNode}s are cached
     * in this map.
     * </p>
     * <p>
     * The longer the application runs, the more stable this mapping will
     * become. so we don't concurrent map but a lock. as this lock only happens
     * at the very beginning while concurrent map will hold the lock all the
     * time
     * </p>
     * <p>
     * 请记住，无论在上下文中，相同的resource({@link ResourceWrapper#equals(Object)}) 将全局共享相同的ProcessorSlotChain。
     * 因此，如果代码{@link #entry(Context, ResourceWrapper, DefaultNode, int, Object...)}，则resource名称必须相同，但context名称可能不相同。
     * </p>
     * <p>
     * 要获取不同context中相同resource的总统计信息，同一resource将全局共享同一个ClusterNode。 所有ClusterNode都缓存在此映射中。
     * </p>
     * <p>
     * 应用程序运行的时间越长，映射就越稳定。 所以我们不会同时映射而是锁定。 因为这个锁只发生在最开始，而并发映射将始终保持锁
     * </p>
     */
    private static volatile Map<ResourceWrapper, ClusterNode> clusterNodeMap
            = new HashMap<ResourceWrapper, ClusterNode>();

    private static final Object lock = new Object();

    private ClusterNode clusterNode = null;

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, Object... args)
            throws Throwable {
        if (clusterNode == null) { //不存在进行初始化
            synchronized (lock) {
                if (clusterNode == null) {
                    // Create the cluster node.
                    // 创建 cluster node.
                    clusterNode = Env.nodeBuilder.buildClusterNode();
                    HashMap<ResourceWrapper, ClusterNode> newMap = new HashMap<ResourceWrapper, ClusterNode>(16);
                    newMap.putAll(clusterNodeMap);
                    newMap.put(node.getId(), clusterNode);

                    clusterNodeMap = newMap;
                }
            }
        }
        //为node设置ClusterNode
        node.setClusterNode(clusterNode);

        /*
         * if context origin is set, we should get or create a new {@link Node} of
         * the specific origin.
         * <p>
         *     将此字符串与指定的对象进行比较。 当且仅当参数不为null并且是表示与此对象相同的字符序列的String对象时，结果才为真。
         */
        if (!"".equals(context.getOrigin())) { //根据原点进行管理
            Node originNode = node.getClusterNode().getOriginNode(context.getOrigin());
            context.getCurEntry().setOriginNode(originNode); //为当前的entry设置originNode
        }

        fireEntry(context, resourceWrapper, node, count, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }

    /**
     * Get {@link ClusterNode} of the resource of the specific type.
     *
     * @param id   resource name.
     * @param type invoke type.
     * @return the {@link ClusterNode}
     */
    public static ClusterNode getClusterNode(String id, EntryType type) {
        return clusterNodeMap.get(new StringResourceWrapper(id, type));
    }

    /**
     * Get {@link ClusterNode} of the resource name.
     *
     * @param id resource name.
     * @return the {@link ClusterNode}.
     */
    public static ClusterNode getClusterNode(String id) {
        if (id == null) {
            return null;
        }
        ClusterNode clusterNode = null;

        for (EntryType nodeType : EntryType.values()) {
            clusterNode = clusterNodeMap.get(new StringResourceWrapper(id, nodeType));
            if (clusterNode != null) {
                break;
            }
        }

        return clusterNode;
    }

    /**
     * Get {@link ClusterNode}s map, this map holds all {@link ClusterNode}s, it's key is resource name,
     * value is the related {@link ClusterNode}. <br/>
     * DO NOT MODIFY the map returned.
     *
     * @return all {@link ClusterNode}s
     */
    public static Map<ResourceWrapper, ClusterNode> getClusterNodeMap() {
        return clusterNodeMap;
    }

}
