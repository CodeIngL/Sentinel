package com.alibaba.csp.sentinel.slots;

import com.alibaba.csp.sentinel.slotchain.DefaultProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slots.block.authority.AuthoritySlot;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeSlot;
import com.alibaba.csp.sentinel.slots.block.flow.FlowSlot;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import com.alibaba.csp.sentinel.slots.logger.LogSlot;
import com.alibaba.csp.sentinel.slots.nodeselector.NodeSelectorSlot;
import com.alibaba.csp.sentinel.slots.statistic.StatisticSlot;
import com.alibaba.csp.sentinel.slots.system.SystemSlot;

/**
 * Helper class to create {@link ProcessorSlotChain}.
 * <p>
 * <p>
 * 创建{@link ProcessorSlotChain}的工具类，默认带上所有的插槽。
 * </p>
 * <p>
 * 你可以添加额外的插槽。
 * </p>
 * <p>
 * <p>
 * 插槽方向
 * </p>
 * <p>
 * {@link NodeSelectorSlot}===>
 * {@link ClusterBuilderSlot}===>
 * {@link LogSlot}===>
 * {@link StatisticSlot}===>
 * {@link AuthoritySlot}===>
 * {@link FlowSlot}===>
 * {@link DegradeSlot}
 * </p>
 *
 * @author qinan.qn
 * @author leyou
 */
public class DefaultSlotsChainBuilder implements SlotsChainBuilder {

    @Override
    public ProcessorSlotChain build() {
        ProcessorSlotChain chain = new DefaultProcessorSlotChain();
        chain.addLast(new NodeSelectorSlot());
        chain.addLast(new ClusterBuilderSlot());
        chain.addLast(new LogSlot());
        chain.addLast(new StatisticSlot());
        chain.addLast(new SystemSlot());
        chain.addLast(new AuthoritySlot());
        chain.addLast(new FlowSlot());
        chain.addLast(new DegradeSlot());

        return chain;
    }

}
