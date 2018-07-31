package com.alibaba.csp.sentinel.slots.statistic.base;

/**
 * Represents metrics data in a period of time window.
 * <p>
 * 表示时间窗口中的一段度量数据。
 * </p>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class Window {

    //通过
    private final LongAdder pass = new LongAdder();
    //阻塞
    private final LongAdder block = new LongAdder();
    //异常
    private final LongAdder exception = new LongAdder();
    //rt
    private final LongAdder rt = new LongAdder();
    //成功
    private final LongAdder success = new LongAdder();
    //最小rt
    private final LongAdder minRt = new LongAdder();

    public Window() {
        minRt.add(4900);
    }

    /**
     * Clean the adders and reset window to provided start time.
     *
     * @param startTime the start time of the window
     * @return new clean window
     */
    Window resetTo(long startTime) {
        pass.reset();
        block.reset();
        exception.reset();
        rt.reset();
        success.reset();
        minRt.reset();
        return this;
    }

    public long pass() {
        return pass.sum();
    }

    public long block() {
        return block.sum();
    }

    public long exception() {
        return exception.sum();
    }

    public long rt() {
        return rt.sum();
    }

    public long minRt() {
        return minRt.longValue();
    }

    public long success() {
        return success.sum();
    }

    public void addPass() {
        pass.add(1L);
    }

    public void addException() {
        exception.add(1L);
    }

    public void addBlock() {
        block.add(1L);
    }

    public void addSuccess() {
        success.add(1L);
    }

    public void addRT(long rt) {
        this.rt.add(rt);

        if (minRt.longValue() > rt) {
            minRt.internalReset(rt);
        }
    }
}
