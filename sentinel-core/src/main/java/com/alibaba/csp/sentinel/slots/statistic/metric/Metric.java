package com.alibaba.csp.sentinel.slots.statistic.metric;

import java.util.List;

import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.slots.statistic.base.Window;

/**
 * Represents a basic structure recording invocation metrics of protected resources.
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public interface Metric {

    /**
     * Get total success count.
     *
     * @return success count
     */
    long success();

    long maxSuccess();

    /**
     * Get total exception count.
     *
     * @return exception count
     */
    long exception();

    /**
     * Get total block count.
     *
     * @return block count
     */
    long block();

    /**
     * Get total pass count.
     *
     * @return pass count
     */
    long pass();

    /**
     * Get total RT.
     *
     * @return total RT
     */
    long rt();

    /**
     * Get the minimal RT.
     *
     * @return minimal RT
     */
    long minRt();

    List<MetricNode> details();

    /**
     * Get the raw window array.
     *
     * @return window metric array
     */
    Window[] windows();

    /**
     * Increment by one the current exception count.
     */
    void addException();

    /**
     * Increment by one the current blovk count.
     */
    void addBlock();

    /**
     * Increment by one the current success count.
     */
    void addSuccess();

    /**
     * Increment by one the current pass count.
     */
    void addPass();

    /**
     * Add given RT to current total RT.
     *
     * @param rt RT
     */
    void addRT(long rt);

    // Tool methods.

    void debugQps();

    long previousWindowBlock();

    long previousWindowPass();
}
