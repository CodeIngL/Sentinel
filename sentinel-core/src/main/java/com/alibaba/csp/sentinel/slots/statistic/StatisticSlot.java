package com.alibaba.csp.sentinel.slots.statistic;

import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * <p>
 * A processor slot that dedicates to real time statistics.
 * When entering this slot, we need to separately count the following
 * information:
 * <ul>
 * <li>{@link ClusterNode}: total statistics of a cluster node of the resource id  </li>
 * <li> origin node: statistics of a cluster node from different callers/origins.</li>
 * <li> {@link DefaultNode}: statistics for specific resource name in the specific context.
 * <li> Finally, the sum statistics of all entrances.</li>
 * </ul>
 * </p>
 * <p>
 * <p>
 * 专用于实时统计的处理器插槽。 进入此插槽时，我们需要单独计算以下信息：
 * <ul>
 * <li>{@link ClusterNode}: resource id的cluster node的总统计信息 </li>
 * <li> origin node: 来自不同callers/origins的cluster node的统计信息. </li>
 * <li> {@link DefaultNode}: 特定上下文中特定resource name的统计信息。.
 * <li> 最后，所有entrances的总和统计</li>
 * </ul>
 * </p>
 *
 * @author jialiang.linjl
 */
public class StatisticSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, Object... args)
            throws Throwable {

        try {
            fireEntry(context, resourceWrapper, node, count, args);
            node.increaseThreadNum();
            node.addPassRequest();

            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().increaseThreadNum();
                context.getCurEntry().getOriginNode().addPassRequest();
            }

            if (resourceWrapper.getType() == EntryType.IN) {
                Constants.ENTRY_NODE.increaseThreadNum();
                Constants.ENTRY_NODE.addPassRequest();
            }

        } catch (BlockException e) {
            context.getCurEntry().setError(e);

            // Add block count.
            node.increaseBlockedQps();
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().increaseBlockedQps();
            }

            if (resourceWrapper.getType() == EntryType.IN) {
                Constants.ENTRY_NODE.increaseBlockedQps();
            }

            throw e;
        } catch (Throwable e) {
            context.getCurEntry().setError(e);

            // Should not happen
            node.increaseExceptionQps();
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().increaseExceptionQps();
            }

            if (resourceWrapper.getType() == EntryType.IN) {
                Constants.ENTRY_NODE.increaseExceptionQps();
            }
            throw e;
        }
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        DefaultNode node = (DefaultNode) context.getCurNode();

        if (context.getCurEntry().getError() == null) {
            long rt = TimeUtil.currentTimeMillis() - context.getCurEntry().getCreateTime();
            if (rt > Constants.TIME_DROP_VALVE) {
                rt = Constants.TIME_DROP_VALVE;
            }

            node.rt(rt);
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().rt(rt);
            }

            node.decreaseThreadNum();

            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().decreaseThreadNum();
            }

            if (resourceWrapper.getType() == EntryType.IN) {
                Constants.ENTRY_NODE.rt(rt);
                Constants.ENTRY_NODE.decreaseThreadNum();
            }
        } else {
            // error may happen
            // node.rt(-2);
        }

        fireExit(context, resourceWrapper, count);
    }

}
