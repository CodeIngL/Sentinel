package com.alibaba.csp.sentinel.slots.statistic.metric;

import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.csp.sentinel.slots.statistic.base.LeapArray;
import com.alibaba.csp.sentinel.slots.statistic.base.Window;
import com.alibaba.csp.sentinel.slots.statistic.base.WindowWrap;

/**
 * The fundamental data structure for metric statistics in a time window.
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class WindowLeapArray extends LeapArray<Window> {

    private final int timeLength;

    public WindowLeapArray(int windowLengthInMs, int intervalInSec) {
        super(windowLengthInMs, intervalInSec);
        timeLength = intervalInSec * 1000;
    }

    private ReentrantLock addLock = new ReentrantLock();

    @Override
    public WindowWrap<Window> currentWindow(long time) {

        long timeId = time / windowLength;
        // Calculate current index.
        int idx = (int)(timeId % array.length());

        // Cut the time to current window start.
        time = time - time % windowLength;

        while (true) {
            WindowWrap<Window> old = array.get(idx);
            if (old == null) {
                WindowWrap<Window> window = new WindowWrap<Window>(windowLength, time, new Window());
                if (array.compareAndSet(idx, null, window)) {
                    return window;
                } else {
                    Thread.yield();
                }
            } else if (time == old.windowStart()) {
                return old;
            } else if (time > old.windowStart()) {
                if (addLock.tryLock()) {
                    try {
                        WindowWrap<Window> window = new WindowWrap<Window>(windowLength, time, new Window());
                        if (array.compareAndSet(idx, old, window)) {
                            for (int i = 0; i < array.length(); i++) {
                                WindowWrap<Window> tmp = array.get(i);
                                if (tmp == null) {
                                    continue;
                                } else {
                                    if (tmp.windowStart() < time - timeLength) {
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

            } else if (time < old.windowStart()) {
                return new WindowWrap<Window>(windowLength, time, new Window());
            }
        }
    }
}
