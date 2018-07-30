package com.alibaba.csp.sentinel.slotchain;

import com.alibaba.csp.sentinel.context.Context;

/**
 * A container of some process and ways of notification when the process is finished.
 *
 * <p>
 *     处理完成时某个处理器的容器（槽）和通知方式。
 * </p>
 *
 *
 * @author qinan.qn
 * @author jialiang.linjl
 * @author leyou(lihao)
 */
public interface ProcessorSlot<T> {

    /**
     * Entrance of this slot.
     * <p>
     * 进入槽的方法
     *
     * @param context         current {@link Context}
     * @param resourceWrapper current resource
     * @param param           Generics parameter, usually is a {@link com.alibaba.csp.sentinel.node.Node}
     * @param count           tokens needed
     * @param args            parameters of the original call
     * @throws Throwable
     */
    void entry(Context context, ResourceWrapper resourceWrapper, T param, int count, Object... args)
        throws Throwable;

    /**
     * Means finish of {@link #entry(Context, ResourceWrapper, Object, int, Object...)}.
     * <p>
     * 进入后出发进入事件
     *
     * @param context         current {@link Context}
     * @param resourceWrapper current resource
     * @param obj
     * @param count           tokens needed
     * @param args            parameters of the original call
     * @throws Throwable
     */
    void fireEntry(Context context, ResourceWrapper resourceWrapper, Object obj, int count, Object... args)
        throws Throwable;

    /**
     * Exit of this slot.
     * <p>
     * 离开（结束）这个槽
     *
     * @param context         current {@link Context}
     * @param resourceWrapper current resource
     * @param count           tokens needed
     * @param args            parameters of the original call
     */
    void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args);

    /**
     * Means finish of {@link #exit(Context, ResourceWrapper, int, Object...)}.
     * <p>
     * 离开（结束）这个槽触发事件
     *
     * @param context         current {@link Context}
     * @param resourceWrapper current resource
     * @param count           tokens needed
     * @param args            parameters of the original call
     */
    void fireExit(Context context, ResourceWrapper resourceWrapper, int count, Object... args);
}
