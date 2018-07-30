package com.alibaba.csp.sentinel.slotchain;

import com.alibaba.csp.sentinel.context.Context;

/**
 * <p>
 * {@link ProcessorSlotChain}的默认实现，一个处理槽的管道（链）
 * </p>
 *
 * @author qinan.qn
 * @author jialiang.linjl
 */
public class DefaultProcessorSlotChain extends ProcessorSlotChain {

    //初始化
    AbstractLinkedProcessorSlot<?> first = new AbstractLinkedProcessorSlot<Object>() {

        @Override
        public void entry(Context context, ResourceWrapper resourceWrapper, Object t, int count, Object... args)
                throws Throwable {
            super.fireEntry(context, resourceWrapper, t, count, args);
        }

        @Override
        public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
            super.fireExit(context, resourceWrapper, count, args);
        }

    };
    AbstractLinkedProcessorSlot<?> end = first;

    /**
     * 头部添加
     *
     * @param protocolProcessor processor to be added.
     */
    @Override
    public void addFirst(AbstractLinkedProcessorSlot<?> protocolProcessor) {
        protocolProcessor.setNext(first.getNext());
        first.setNext(protocolProcessor);
        if (end == first) {
            end = protocolProcessor;
        }
    }

    /**
     * 尾部添加
     *
     * @param protocolProcessor processor to be added.
     */
    @Override
    public void addLast(AbstractLinkedProcessorSlot<?> protocolProcessor) {
        end.setNext(protocolProcessor);
        end = protocolProcessor;
    }

    /**
     * Same as {@link #addLast(AbstractLinkedProcessorSlot)}.
     * <p>
     * 正常的next设置则是添加到尾部
     *
     * @param next processor to be added.
     */
    @Override
    public void setNext(AbstractLinkedProcessorSlot<?> next) {
        addLast(next);
    }

    @Override
    public AbstractLinkedProcessorSlot<?> getNext() {
        return first.getNext();
    }

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, Object t, int count, Object... args)
            throws Throwable {
        first.transformEntry(context, resourceWrapper, t, count, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        first.exit(context, resourceWrapper, count, args);
    }

}
