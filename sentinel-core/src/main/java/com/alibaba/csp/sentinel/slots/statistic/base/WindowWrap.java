package com.alibaba.csp.sentinel.slots.statistic.base;

/**
 * Wrapper entity class for a period of time window.
 * <p>
 * 包含一段时间窗口的实体类。
 * </p>
 *
 * @param <T> data type
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class WindowWrap<T> {

    /**
     * The length of the window.
     * 窗口的长度
     */
    private final long windowLength;

    /**
     * Start time of the window in milliseconds.
     * 窗口的开始时间
     */
    private long windowStart;

    /**
     * Statistic value.
     * 性能值
     */
    private T value;

    /**
     * 构造函数
     * @param windowLength the time length of the window 时间窗口长度
     * @param windowStart  the start timestamp of the window  窗口的起始值
     * @param value        window data 窗口内容
     */
    public WindowWrap(long windowLength, long windowStart, T value) {
        this.windowLength = windowLength;
        this.windowStart = windowStart;
        this.value = value;
    }

    public long windowLength() {
        return windowLength;
    }

    public long windowStart() {
        return windowStart;
    }

    public T value() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
