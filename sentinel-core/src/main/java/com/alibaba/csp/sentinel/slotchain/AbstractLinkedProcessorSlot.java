package com.alibaba.csp.sentinel.slotchain;

import com.alibaba.csp.sentinel.context.Context;

/**
 * <p>
 * 处理槽的抽象实现，并且呈现了结构是一个单向的链表
 * </p>
 * <p>
 * 由于实现了处理槽，同时符合链表的结构，很容易设计为一种管式触发事件的control flow
 * 这里正是如此
 * </p>
 *
 * @author qinan.qn
 * @author jialiang.linjl
 */
public abstract class AbstractLinkedProcessorSlot<T> implements ProcessorSlot<T> {

    private AbstractLinkedProcessorSlot<?> next = null;

    /**
     * <p>
     * 触发进入事件的管道control flow
     *
     * @param context         current {@link Context}
     * @param resourceWrapper current resource
     * @param obj
     * @param count           tokens needed
     * @param args            parameters of the original call
     * @throws Throwable
     */
    @Override
    public void fireEntry(Context context, ResourceWrapper resourceWrapper, Object obj, int count, Object... args)
            throws Throwable {
        if (next != null) {
            next.transformEntry(context, resourceWrapper, obj, count, args);
        }
    }

    @SuppressWarnings("unchecked")
    void transformEntry(Context context, ResourceWrapper resourceWrapper, Object o, int count, Object... args)
            throws Throwable {
        T t = (T) o;
        entry(context, resourceWrapper, t, count, args);
    }

    /**
     * 触发离开(结束)事件的管道control flow
     *
     * @param context         current {@link Context}
     * @param resourceWrapper current resource
     * @param count           tokens needed
     * @param args            parameters of the original call
     */
    @Override
    public void fireExit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        if (next != null) {
            next.exit(context, resourceWrapper, count, args);
        }
    }

    //============================这里，我们打链表需要======================================

    public AbstractLinkedProcessorSlot<?> getNext() {
        return next;
    }

    public void setNext(AbstractLinkedProcessorSlot<?> next) {
        this.next = next;
    }

}
