package com.alibaba.csp.sentinel.slots.logger;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * A {@link com.alibaba.csp.sentinel.slotchain.ProcessorSlot} that is response for logging block exceptions
 * to provide concrete logs for troubleshooting.
 * <p>
 * {@link com.alibaba.csp.sentinel.slotchain.ProcessorSlot} 它是记录block exceptions，以提供用于故障排除的具体日志。
 * </p>
 */
public class LogSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode obj, int count, Object... args)
            throws Throwable {
        try {
            fireEntry(context, resourceWrapper, obj, count, args);
        } catch (BlockException e) {
            //记录日志，鹰眼追踪的日志记录
            EagleEyeLogUtil.log(resourceWrapper.getName(), e.getClass().getSimpleName(), e.getRuleLimitApp(),
                    context.getOrigin(), count);
            throw e;
        } catch (Throwable e) {
            RecordLog.info("Entry exception", e);
        }

    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        try {
            fireExit(context, resourceWrapper, count, args);
        } catch (Throwable e) {
            RecordLog.info("Entry exit exception", e);
        }
    }
}
