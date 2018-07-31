package com.alibaba.csp.sentinel.node;

import java.util.Map;

import com.alibaba.csp.sentinel.node.metric.MetricNode;

/**
 * This class holds real-time statistics for a resource.
 * <p>此class持有resource的实时统计（statistics）信息</p>
 *
 * @author qinan.qn
 * @author leyou
 */
public interface Node {

    /**
     * Incoming request per minute.
     * <p>
     * 每分钟传入请求。
     * </p>
     */
    long totalRequest();

    long totalSuccess();

    /**
     * Blocked request count per minute.
     * <p>
     * 每分钟被阻止的请求数。
     * </p>
     */
    long blockedRequest();

    /**
     * Exception count per minute.
     * <p>
     * 每分钟异常计数。
     * </p>
     */
    long totalException();

    /**
     * Incoming request per second.
     * <p>
     * 每秒传入请求。
     * </p>
     */
    long passQps();

    /**
     * Blocked request per second.
     * <p>
     * 每秒被阻止的请求。
     * </p>
     */
    long blockedQps();

    /**
     * Incoming request + block request per second.
     * <p>
     * 每秒传入请求+阻止请求。
     * </p>
     */
    long totalQps();

    /**
     * Outgoing request per second.
     * <p>
     * 每秒传出请求。
     * </p>
     */
    long successQps();

    long maxSuccessQps();

    /**
     * Exception count per second.
     * <p>
     * 每秒异常计数。
     * </p>
     */
    long exceptionQps();

    /**
     * Average response per second.
     * <p>
     * 每秒平均响应次数。
     * </p>
     */
    long avgRt();

    long minRt();

    /**
     * Current active thread.
     * <p>
     * 当前活动线程数
     * </p>
     */
    int curThreadNum();

    /**
     * Last seconds block QPS.
     * <p>
     * 最后几秒阻止QPS。
     * </p>
     */
    long previousBlockQps();

    /**
     * Last window QPS.
     * <p>
     * 最后一个窗口QPS。
     * </p>
     */
    long previousPassQps();

    Map<Long, MetricNode> metrics();

    void addPassRequest();

    void rt(long rt);

    void increaseBlockedQps();

    void increaseExceptionQps();

    void increaseThreadNum();

    void decreaseThreadNum();

    /**
     * Reset the internal counter.
     * <p>
     * 重置内部计数器。
     * </p>
     */
    void reset();

    /**
     * Debug only.
     * <p>
     * 仅调试。
     * </p>
     */
    void debug();
}
