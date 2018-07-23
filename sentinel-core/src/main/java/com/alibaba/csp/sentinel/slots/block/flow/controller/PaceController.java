package com.alibaba.csp.sentinel.slots.block.flow.controller;

import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.csp.sentinel.slots.block.flow.Controller;

import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.csp.sentinel.node.Node;

/**
 * @author jialiang.linjl
 */
public class PaceController implements Controller {

    private final int maxQueueingTimeMs;
    private final double count;
    private final AtomicLong latestPassedTime = new AtomicLong(-1);

    public PaceController(int timeOut, double count) {
        this.maxQueueingTimeMs = timeOut;
        this.count = count;
    }

    @Override
    public boolean canPass(Node node, int acquireCount) {

        // 按照斜率来计算计划中应该什么时候通过
        long currentTime = TimeUtil.currentTimeMillis();

        long costTime = Math.round(1.0 * (acquireCount) / count * 1000);

        //期待时间
        long expectedTime = costTime + latestPassedTime.get();

        if (expectedTime <= currentTime) {
            //这里会有冲突,然而冲突就冲突吧.
            latestPassedTime.set(currentTime);
            return true;
        } else {
            // 计算自己需要的等待时间
            long waitTime = costTime + latestPassedTime.get() - TimeUtil.currentTimeMillis();
            if (waitTime >= maxQueueingTimeMs) {
                return false;
            } else {
                long oldTime = latestPassedTime.addAndGet(costTime);
                try {
                    waitTime = oldTime - TimeUtil.currentTimeMillis();
                    if (waitTime >= maxQueueingTimeMs) {
                        latestPassedTime.addAndGet(-costTime);
                        return false;
                    }
                    Thread.sleep(waitTime);
                    return true;
                } catch (InterruptedException e) {
                }
            }
        }

        return false;
    }

}
