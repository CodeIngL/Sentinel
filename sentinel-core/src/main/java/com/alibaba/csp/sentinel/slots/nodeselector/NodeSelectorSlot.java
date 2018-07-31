package com.alibaba.csp.sentinel.slots.nodeselector;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.csp.sentinel.Env;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.EntranceNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;

/**
 * </p>
 * This class will try to build the calling traces via
 * <ol>
 * <li>adding a new {@link DefaultNode} if needed as the last child in the context.
 * The context's last node is the current node or the parent node of the context. </li>
 * <li>setting itself to the the context current node.</li>
 * </ol>
 * </p>
 * <p>
 * <p>It works as follow:</p>
 * <pre>
 * ContextUtil.enter("entrance1", "appA");
 * Entry nodeA = SphU.entry("nodeA");
 * if (nodeA != null) {
 *     nodeA.exit();
 * }
 * ContextUtil.exit();
 * </pre>
 * <p>
 * Above code will generate the following invocation structure in memory:
 * <p>
 * <pre>
 *
 *              machine-root
 *                  /
 *                 /
 *           EntranceNode1
 *               /
 *              /
 *        DefaultNode(nodeA)- - - - - -> ClusterNode(nodeA);
 * </pre>
 * <p>
 * <p>
 * Here the {@link EntranceNode} represents "entrance1" given by
 * {@code ContextUtil.enter("entrance1", "appA")}.
 * </p>
 * <p>
 * Both DefaultNode(nodeA) and ClusterNode(nodeA) holds statistics of "nodeA", which is given
 * by {@code SphU.entry("nodeA")}
 * </p>
 * <p>
 * The {@link ClusterNode} is uniquely identified by the ResourceId; the {@link DefaultNode}
 * is identified by both the resource id and {@link Context}. In other words, one resource
 * id will generate multiple {@link DefaultNode} for each distinct context, but only one
 * {@link ClusterNode}.
 * </p>
 * <p>
 * the following code shows one resource id in two different context:
 * </p>
 * <p>
 * <pre>
 *    ContextUtil.enter("entrance1", "appA");
 *    Entry nodeA = SphU.entry("nodeA");
 *    if (nodeA != null) {
 *        nodeA.exit();
 *    }
 *    ContextUtil.exit();
 *
 *    ContextUtil.enter("entrance2", "appA");
 *    nodeA = SphU.entry("nodeA");
 *    if (nodeA != null) {
 *        nodeA.exit();
 *    }
 *    ContextUtil.exit();
 * </pre>
 * <p>
 * Above code will generate the following invocation structure in memory:
 * <p>
 * <pre>
 *
 *                  machine-root
 *                  /         \
 *                 /           \
 *         EntranceNode1   EntranceNode2
 *               /               \
 *              /                 \
 *      DefaultNode(nodeA)   DefaultNode(nodeA)
 *             |                    |
 *             +- - - - - - - - - - +- - - - - - -> ClusterNode(nodeA);
 * </pre>
 * <p>
 * <p>
 * As we can see, two {@link DefaultNode} are created for "nodeA" in two context, but only one
 * {@link ClusterNode} is created.
 * </p>
 * <p>
 * <p>
 * We can also check this structure by calling: <br/>
 * {@code curl http://localhost:8719/tree?type=root}
 * </p>
 * <p>
 * 该类将尝试通过构建调用跟踪
 * <ol>
 * <li>如果需要，添加新的{@link DefaultNode}作为上下文中的最后一个子节点。 上下文的最后一个节点是当前节点或上下文的父节点。</li>
 * <li>将自身设置为上下文当前节点。</li>
 * </ol>
 * <p>
 * 它的工作原理如下：
 * </p>
 * <pre>
 * ContextUtil.enter("entrance1", "appA");
 * Entry nodeA = SphU.entry("nodeA");
 * if (nodeA != null) {
 *     nodeA.exit();
 * }
 * ContextUtil.exit();
 * </pre>
 * <p>
 * <p>
 * 上面的代码将在内存中生成以下调用结构：
 * <p>
 * <pre>
 *
 *              machine-root
 *                  /
 *                 /
 *           EntranceNode1
 *               /
 *              /
 *        DefaultNode(nodeA)- - - - - -> ClusterNode(nodeA);
 * </pre>
 * <p>
 * <p>
 * <p>
 * 这里的{@link EntranceNode}表示由{@code ContextUtil.enter("entrance1", "appA")}给出的“entrance1”。
 * DefaultNode（nodeA）和ClusterNode（nodeA）都包含“nodeA”的统计信息，由{@code SphU.entry("nodeA")}给出
 * {@link ClusterNode} 由ResourceId唯一标识; {@link DefaultNode}由ResourceId和Context标识。
 * 换句话说，一个ResourceId将为每个不同的Context生成多个{@link DefaultNode}，但只生成一个{@link ClusterNode}。
 * 以下代码显示了两个不同上下文中的一个ResourceId：
 * </p>
 * <pre>
 *    ContextUtil.enter("entrance1", "appA");
 *    Entry nodeA = SphU.entry("nodeA");
 *    if (nodeA != null) {
 *        nodeA.exit();
 *    }
 *    ContextUtil.exit();
 *
 *    ContextUtil.enter("entrance2", "appA");
 *    nodeA = SphU.entry("nodeA");
 *    if (nodeA != null) {
 *        nodeA.exit();
 *    }
 *    ContextUtil.exit();
 * </pre>
 * <p>
 * 上面的代码将在内存中生成以下调用结构：
 * </p>
 * <pre>
 *
 *                  machine-root
 *                  /         \
 *                 /           \
 *         EntranceNode1   EntranceNode2
 *               /               \
 *              /                 \
 *      DefaultNode(nodeA)   DefaultNode(nodeA)
 *             |                    |
 *             +- - - - - - - - - - +- - - - - - -> ClusterNode(nodeA);
 * </pre>
 * <p>
 * 我们可以看到，在两个上下文中为“nodeA”创建了两个{@link DefaultNode}，但只创建了一个{@link ClusterNode}。
 * </p>
 * <p><br/>
 * {@code curl http://localhost:8719/tree?type=root}
 *
 * @author jialiang.linjl
 * @see EntranceNode
 * @see ContextUtil
 */
public class NodeSelectorSlot extends AbstractLinkedProcessorSlot<Object> {

    /**
     * {@link DefaultNode}s of the same resource in different context.
     * <p>
     * {@link DefaultNode}在不同上下文中的相同资源。
     * </p>
     */
    private Map<String, DefaultNode> map = new HashMap<String, DefaultNode>(10);

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, Object obj, int count, Object... args)
            throws Throwable {
        /*
         * It's interesting that we use context name rather resource name as the map key.
         *
         * Remember that same resource({@link ResourceWrapper#equals(Object)}) will share
         * the same {@link ProcessorSlotChain} globally, no matter in which context. So if
         * code goes into {@link #entry(Context, ResourceWrapper, DefaultNode, int, Object...)},
         * the resource name must be same but context name may not.
         *
         * If we use {@link com.alibaba.csp.sentinel.SphU#entry(String resource)} to
         * enter same resource in different context, using context name as map key can
         * distinguish the same resource. In this case, multiple {@link DefaultNode}s will be created
         * of the same resource name, for every distinct context (different context name) each.
         *
         * Consider another question. One resource may have multiple {@link DefaultNode},
         * so what is the fastest way to get total statistics of the same resource?
         * The answer is all {@link DefaultNode}s with same resource name share one
         * {@link ClusterNode}. See {@link ClusterBuilderSlot} for detail.
         */
        /**
         * <p>
         * 有趣的是，我们使用上下文名称而不是资源名称作为映射键。
         * </p>
         * 请记住，无论在哪个上下文中，相同的resource({@link ResourceWrapper#equals(Object)})将全局共享相同的ProcessorSlotChain。
         * 因此，如果代码{@link #entry(Context, ResourceWrapper, DefaultNode, int, Object...)}，则resource名称必须相同，但context名称可能不相同。
         * <p>
         * 如果我们使用{@link com.alibaba.csp.sentinel.SphU#entry(String resource)} 在不同的context中输入相同的resource，则使用context名称作为map键可以区分相同的resource。
         * 在这种情况下，将为每个不同的context（不同的context名称）创建多个相同resource名称的{@link DefaultNode}。
         * <p>
         * 考虑另一个问题。 一个resource可能有多个DefaultNode，那么获取同一resource的总统计信息的最快方法是什么？
         * 答案是所有具有相同resource名称的DefaultNodes共享一个ClusterNode。
         * 有关详细信息，请参见ClusterBuilderSlot。
         */
        DefaultNode node = map.get(context.getName());//缓存中获得
        if (node == null) {//没有，进行创建
            synchronized (this) {
                node = map.get(context.getName()); //使用名字来识别
                if (node == null) { //构建Node整个,map替换掉
                    node = Env.nodeBuilder.buildTreeNode(resourceWrapper, null);
                    HashMap<String, DefaultNode> cacheMap = new HashMap<String, DefaultNode>(map.size());
                    cacheMap.putAll(map);
                    cacheMap.put(context.getName(), node);
                    map = cacheMap;
                }
                // Build invocation tree
                // 构建调用树
                ((DefaultNode) context.getLastNode()).addChild(node);
            }
        }

        //为上下文的当前Entry设置当前的DefaultNode
        context.setCurNode(node);
        fireEntry(context, resourceWrapper, node, count, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }
}
