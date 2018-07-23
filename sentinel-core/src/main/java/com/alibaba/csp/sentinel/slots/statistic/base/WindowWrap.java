package com.alibaba.csp.sentinel.slots.statistic.base;

/**
 * Wrapper entity class for a period of time window.
 *
 * @param <T> data type
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class WindowWrap<T> {

    /**
     * The length of the window.
     */
    private final long windowLength;

    /**
     * Start time of the window in milliseconds.
     */
    private long windowStart;

    /**
     * Statistic value.
     */
    private T value;

    /**
     * @param windowLength the time length of the window
     * @param windowStart  the start timestamp of the window
     * @param value        window data
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
