package com.alibaba.csp.sentinel.node;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.nodeselector.NodeSelectorSlot;

/**
 * <p>
 * A {@link Node} represents the entrance of the invocation tree.
 * </p>
 * <p>
 * One {@link Context} will related to a {@link EntranceNode},
 * which represents the entrance of the invocation tree. New {@link EntranceNode} will be created if
 * current context does't have one. Note that same context name will share same {@link EntranceNode}
 * globally.
 * </p>
 * <p>
 * <p>
 * {@link Node}表示调用树的入口。
 * </p>
 * <p>
 * 一个{@link Context}将与{@link EntranceNode}相关，该EntryNode表示调用树的入口。
 * 如果当前上下文没有，则将创建新的EntranceNode。 请注意，相同的上下文名称将全局共享相同的EntranceNode。
 * <p>
 * </p>
 *
 * @author qinan.qn
 * @see ContextUtil
 * @see ContextUtil#enter(String, String)
 * @see NodeSelectorSlot
 */
public class EntranceNode extends DefaultNode {

    public EntranceNode(ResourceWrapper id, ClusterNode clusterNode) {
        super(id, clusterNode);
    }

    @Override
    public long avgRt() {
        long rt = 0;
        long totalQps = 0;
        for (Node node : getChildList()) {
            rt += node.avgRt() * node.passQps();
            totalQps += node.passQps();
        }
        return rt / (totalQps == 0 ? 1 : totalQps);
    }

    @Override
    public long blockedQps() {
        int blockQps = 0;
        for (Node node : getChildList()) {
            blockQps += node.blockedQps();
        }
        return blockQps;
    }

    @Override
    public long blockedRequest() {
        long r = 0;
        for (Node node : getChildList()) {
            r += node.blockedRequest();
        }
        return r;
    }

    @Override
    public int curThreadNum() {
        int r = 0;
        for (Node node : getChildList()) {
            r += node.curThreadNum();
        }
        return r;
    }

    @Override
    public long totalQps() {
        int r = 0;
        for (Node node : getChildList()) {
            r += node.totalQps();
        }
        return r;
    }

    @Override
    public long successQps() {
        int r = 0;
        for (Node node : getChildList()) {
            r += node.successQps();
        }
        return r;
    }

    @Override
    public long passQps() {
        int r = 0;
        for (Node node : getChildList()) {
            r += node.passQps();
        }
        return r;
    }

    @Override
    public long totalRequest() {
        long r = 0;
        for (Node node : getChildList()) {
            r += node.totalRequest();
        }
        return r;
    }

}
