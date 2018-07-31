package com.alibaba.csp.sentinel;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * This class is used to record other exception except block exception.
 *
 * <p>
 *     此类用于记录除block exception之外的其他异常。
 * </p>
 *
 * @author jialiang.linjl
 */
public final class Tracer {

    public static void trace(Throwable e) {
        trace(e, 1);
    }

    public static void trace(Throwable e, int count) {
        if (e instanceof BlockException) {
            return;
        }

        Context context = ContextUtil.getContext();
        if (context == null) {
            return;
        }

        DefaultNode curNode = (DefaultNode)context.getCurNode();
        if (curNode == null) {
            return;
        }

        // clusterNode can be null when Constants.ON is false.
        ClusterNode clusterNode = curNode.getClusterNode();
        if (clusterNode == null) {
            return;
        }
        clusterNode.trace(e, count);
    }

}
