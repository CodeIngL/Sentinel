package com.alibaba.csp.sentinel.slots.statistic.metric;

import java.util.List;

import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.slots.statistic.base.Window;

/**
 * Represents a basic structure recording invocation metrics of protected resources.
 * <p>
 * 表示记录受保护资源的调用metrics的基本结构。
 * </p>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public interface Metric {

    /**
     * Get total success count.
     * <p>
     * 获得成功的数量
     * </p>
     *
     * @return success count
     */
    long success();

    long maxSuccess();

    /**
     * Get total exception count.
     * <p>
     * 获得异常的数量
     * </p>
     *
     * @return exception count
     */
    long exception();

    /**
     * Get total block count.
     * <p>获得阻塞的数量</p>
     *
     * @return block count
     */
    long block();

    /**
     * Get total pass count.
     * <p>获得通过的数量</p>
     *
     * @return pass count
     */
    long pass();

    /**
     * Get total RT.
     * <p>获得总共的RT</p>
     *
     * @return total RT
     */
    long rt();

    /**
     * Get the minimal RT.
     * <p>
     * 获得最小的RT
     * </p>
     *
     * @return minimal RT
     */
    long minRt();

    List<MetricNode> details();

    /**
     * Get the raw window array.
     * <p>
     * 获取原始窗口数组。
     * </p>
     *
     * @return window metric array
     */
    Window[] windows();

    /**
     * Increment by one the current exception count.
     * <p>
     * 将当前异常计数增加1。
     * </p>
     */
    void addException();

    /**
     * Increment by one the current block count.
     * <p>
     * 将当前异常计数增加1。
     * </p>
     */
    void addBlock();

    /**
     * Increment by one the current success count.
     * <p>
     * 增加一个当前的成功计数。
     * </p>
     */
    void addSuccess();

    /**
     * Increment by one the current pass count.
     * <p>
     * 将当前通过次数增加1。
     * </p>
     */
    void addPass();

    /**
     * Add given RT to current total RT.
     * <p>
     * 将给定的RT添加到当前的总RT。
     * </p>
     *
     * @param rt RT
     */
    void addRT(long rt);

    // Tool methods. 工具方法。

    void debugQps();

    long previousWindowBlock();

    long previousWindowPass();
}
