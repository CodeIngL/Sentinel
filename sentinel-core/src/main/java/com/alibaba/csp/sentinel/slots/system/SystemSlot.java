package com.alibaba.csp.sentinel.slots.system;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;

/**
 * A {@link ProcessorSlot} that dedicates to {@link SystemRule} checking.
 *
 * <p>
 *     一个专用于{@link SystemRule}检查的{@link ProcessorSlot}。
 * </p>
 *
 * @author jialiang.linjl
 * @author leyou
 */
public class SystemSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, Object... args)
        throws Throwable {
        SystemRuleManager.checkSystem(resourceWrapper);
        fireEntry(context, resourceWrapper, node, count, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }

}
