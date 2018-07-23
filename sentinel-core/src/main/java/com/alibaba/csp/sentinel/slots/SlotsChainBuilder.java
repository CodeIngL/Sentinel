package com.alibaba.csp.sentinel.slots;

import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;

/**
 * @author qinan.qn
 * @author leyou
 */
public interface SlotsChainBuilder {

    /**
     * Helper method to create processor slot chain.
     *
     * @return a processor slot that chain some slots together.
     */
    ProcessorSlotChain build();
}
