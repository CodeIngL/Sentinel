package com.alibaba.csp.sentinel.slots.statistic.metric;

import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.csp.sentinel.slots.statistic.base.LeapArray;
import com.alibaba.csp.sentinel.slots.statistic.base.Window;
import com.alibaba.csp.sentinel.slots.statistic.base.WindowWrap;

/**
 * The fundamental data structure for metric statistics in a time window.
 * <p>
 * 时间窗口中度量标准统计的基础数据结构。
 * </p>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class WindowLeapArray extends LeapArray<Window> {

    //ms数<==>Winodow.intervalInMs
    private final int timeLength;

    public WindowLeapArray(int windowLengthInMs, int intervalInSec) {
        super(windowLengthInMs, intervalInSec);
        timeLength = intervalInSec * 1000; //秒转换为毫秒
    }

    private ReentrantLock addLock = new ReentrantLock();

    /**
     * 当前的窗口
     * @param time
     * @return 时间窗口
     */
    @Override
    public WindowWrap<Window> currentWindow(long time) {

        long timeId = time / windowLength; //过了多少个时间窗口了
        // Calculate current index.
        // 计算在一个时间窗口中的对应索引
        int idx = (int) (timeId % array.length());

        // Cut the time to current window start.
        // 缩短当前窗口启动的时间。
        time = time - time % windowLength;

        while (true) {
            WindowWrap<Window> old = array.get(idx); //获得老值
            if (old == null) { //如果不存在，创建一个
                WindowWrap<Window> window = new WindowWrap<Window>(windowLength, time, new Window());
                if (array.compareAndSet(idx, null, window)) {
                    return window;
                } else {
                    Thread.yield();
                }
            } else if (time == old.windowStart()) { //等于老的时间窗口开始值
                return old; //返回
            } else if (time > old.windowStart()) { //大于，说明越过了这个时间窗口
                if (addLock.tryLock()) {
                    try {
                        WindowWrap<Window> window = new WindowWrap<Window>(windowLength, time, new Window());
                        if (array.compareAndSet(idx, old, window)) {
                            for (int i = 0; i < array.length(); i++) {
                                WindowWrap<Window> tmp = array.get(i);
                                if (tmp == null) {
                                    continue;
                                } else {
                                    if (tmp.windowStart() < time - timeLength) { //过期了直接删除
                                        array.set(i, null);
                                    }
                                }
                            }
                            return window;
                        }
                    } finally {
                        addLock.unlock();
                    }

                } else {
                    Thread.yield();
                }

            } else if (time < old.windowStart()) { //小于老的时间
                return new WindowWrap<Window>(windowLength, time, new Window());
            }
        }
    }
}
