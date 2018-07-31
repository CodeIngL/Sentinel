package com.alibaba.csp.sentinel.node;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.slots.statistic.metric.ArrayMetric;
import com.alibaba.csp.sentinel.slots.statistic.metric.Metric;

/**
 * <p>
 * 带统计信息的node
 * </p>
 *
 * @author qinan.qn
 * @author jialiang.linjl
 */
public class StatisticNode implements Node {

    /**
     * 秒s监控
     */
    private transient Metric rollingCounterInSecond = new ArrayMetric(1000 / SampleCountProperty.sampleCount,
            IntervalProperty.INTERVAL);

    /**
     * 分钟监控
     */
    private transient Metric rollingCounterInMinute = new ArrayMetric(1000, 2 * 60);

    /**
     * 当前的线程数
     */
    private AtomicInteger curThreadNum = new AtomicInteger(0);

    /**
     * 上一次抓取时间
     */
    private long lastFetchTime = -1;

    @Override
    public Map<Long, MetricNode> metrics() {
        long currentTime = TimeUtil.currentTimeMillis();
        currentTime = currentTime - currentTime % 1000;
        Map<Long, MetricNode> metrics = new ConcurrentHashMap<Long, MetricNode>();
        List<MetricNode> minutes = rollingCounterInMinute.details();
        for (MetricNode node : minutes) {
            if (node.getTimestamp() > lastFetchTime && node.getTimestamp() < currentTime) {
                if (node.getPassedQps() != 0 || node.getBlockedQps() != 0) {
                    metrics.put(node.getTimestamp(), node);
                    lastFetchTime = node.getTimestamp();
                }
            }
        }

        return metrics;
    }

    /**
     * 重置
     */
    @Override
    public void reset() {
        rollingCounterInSecond = new ArrayMetric(1000 / SampleCountProperty.sampleCount, IntervalProperty.INTERVAL);
    }

    @Override
    public long totalRequest() {
        long totalRequest = rollingCounterInMinute.pass() + rollingCounterInMinute.block();
        return totalRequest / 2;
    }

    @Override
    public long blockedRequest() {
        return rollingCounterInMinute.block() / 2;
    }

    @Override
    public long blockedQps() {
        return rollingCounterInSecond.block() / IntervalProperty.INTERVAL;
    }

    @Override
    public long previousBlockQps() {
        return this.rollingCounterInMinute.previousWindowBlock();
    }

    @Override
    public long previousPassQps() {
        return this.rollingCounterInMinute.previousWindowPass();
    }

    @Override
    public long totalQps() {
        return passQps() + blockedQps();
    }

    @Override
    public long totalSuccess() {
        return rollingCounterInMinute.success() / 2;
    }

    @Override
    public long exceptionQps() {
        return rollingCounterInSecond.exception() / IntervalProperty.INTERVAL;
    }

    @Override
    public long totalException() {
        return rollingCounterInMinute.exception() / 2;
    }

    @Override
    public long passQps() {
        return rollingCounterInSecond.pass() / IntervalProperty.INTERVAL;
    }

    @Override
    public long successQps() {
        return rollingCounterInSecond.success() / IntervalProperty.INTERVAL;
    }

    @Override
    public long maxSuccessQps() {
        return rollingCounterInSecond.maxSuccess() * SampleCountProperty.sampleCount;
    }

    @Override
    public long avgRt() {
        long successCount = rollingCounterInSecond.success();
        if (successCount == 0) {
            return 0;
        }

        return rollingCounterInSecond.rt() / successCount;
    }

    @Override
    public long minRt() {
        return rollingCounterInSecond.minRt();
    }

    @Override
    public int curThreadNum() {
        return curThreadNum.get();
    }

    @Override
    public void addPassRequest() {
        rollingCounterInSecond.addPass();
        rollingCounterInMinute.addPass();
    }

    @Override
    public void rt(long rt) {
        rollingCounterInSecond.addSuccess();
        rollingCounterInSecond.addRT(rt);

        rollingCounterInMinute.addSuccess();
        rollingCounterInMinute.addRT(rt);
    }

    @Override
    public void increaseBlockedQps() {
        rollingCounterInSecond.addBlock();
        rollingCounterInMinute.addBlock();
    }

    @Override
    public void increaseExceptionQps() {
        rollingCounterInSecond.addException();
        rollingCounterInMinute.addException();

    }

    @Override
    public void increaseThreadNum() {
        curThreadNum.incrementAndGet();
    }

    @Override
    public void decreaseThreadNum() {
        curThreadNum.decrementAndGet();
    }

    @Override
    public void debug() {
        rollingCounterInSecond.debugQps();
    }
}
