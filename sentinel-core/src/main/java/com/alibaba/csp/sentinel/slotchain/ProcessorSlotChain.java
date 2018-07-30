package com.alibaba.csp.sentinel.slotchain;

/**
 * Link all processor slots as a chain.
 *
 * <p>
 *     将所有processor slots链接为chain用于链式处理。如同HttpFilter一样
 * </p>
 *
 * @author qinan.qn
 */
public abstract class ProcessorSlotChain extends AbstractLinkedProcessorSlot<Object> {

    /**
     * Add a processor to the head of this slot chain.
     *
     * <p>
     *     向chain的头部添加处理器
     * </p>
     * @param protocolProcessor processor to be added.
     */
    public abstract void addFirst(AbstractLinkedProcessorSlot<?> protocolProcessor);

    /**
     * Add a processor to the tail of this slot chain.
     *
     * <p>
     *     向chain的尾部添加处理器
     * </p>
     *
     * @param protocolProcessor processor to be added.
     */
    public abstract void addLast(AbstractLinkedProcessorSlot<?> protocolProcessor);
}
