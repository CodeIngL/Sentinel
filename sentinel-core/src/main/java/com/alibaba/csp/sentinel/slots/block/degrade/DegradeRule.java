package com.alibaba.csp.sentinel.slots.block.degrade;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

/**
 * <p>
 * Degrade is used when the resources are in an unstable state, these resources
 * will be degraded within the next defined time window. There are two ways to
 * measure whether a resource is stable or not:
 * </p>
 * <ul>
 * <li>
 * Average response time ({@code DEGRADE_GRADE_RT}): When
 * the average RT exceeds the threshold ('count' in 'DegradeRule', in milliseconds), the
 * resource enters a quasi-degraded state. If the RT of next coming 5
 * requests still exceed this threshold, this resource will be downgraded, which
 * means that in the next time window (defined in 'timeWindow', in seconds) all the
 * access to this resource will be blocked.
 * </li>
 * <li>
 * Exception ratio: When the ratio of exception count per second and the
 * success qps exceeds the threshold, access to the resource will be blocked in
 * the coming window.
 * </li>
 * </ul>
 * <p>
 * <p>
 * 当资源处于不稳定状态时使用降级，这些资源将在下一个定义的时间窗口内降级。 有两种方法可以衡量资源是否稳定：
 * </p>
 * <p>
 * 平均响应时间（DEGRADE_GRADE_RT）：当平均RT超过阈值（'DegradeRule'中的'count'，以毫秒为单位）时，资源进入准降级状态。 如果下一个5个请求的RT仍然超过此阈值，则此资源将被降级，这意味着在下一个时间窗口（在'timeWindow'中定义，以秒为单位）将阻止对该资源的所有访问。
 * </p>
 * <p>
 * 异常率：当每秒异常计数与成功qps的比率超过阈值时，将在下一个窗口中阻止对资源的访问。
 * </p>
 *
 * @author jialiang.linjl
 */
public class DegradeRule extends AbstractRule {

    private static final int RT_MAX_EXCEED_N = 5;

    private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors());

    /**
     * RT threshold or exception ratio threshold count.
     */
    private double count;

    /**
     * Degrade recover timeout (in seconds) when degradation occurs.
     */
    private int timeWindow;

    /**
     * Degrade strategy (0: average RT, 1: exception ratio).
     */
    private int grade = RuleConstant.DEGRADE_GRADE_RT;

    private volatile boolean cut = false;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    private AtomicLong passCount = new AtomicLong(0);

    private final Object lock = new Object();

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public boolean isCut() {
        return cut;
    }

    public void setCut(boolean cut) {
        this.cut = cut;
    }

    public AtomicLong getPassCount() {
        return passCount;
    }

    public void setPassCount(AtomicLong passCount) {
        this.passCount = passCount;
    }

    public int getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DegradeRule)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        DegradeRule that = (DegradeRule) o;

        if (count != that.count) {
            return false;
        }
        if (timeWindow != that.timeWindow) {
            return false;
        }
        if (grade != that.grade) {
            return false;
        }
        // if (cut != that.cut) { return false; }
        //// AtomicLong dose not Override equals()
        // if ((passCount == null && that.passCount != null)
        // || (passCount.get() != that.passCount.get())) {
        // return false;
        // }
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + new Double(count).hashCode();
        result = 31 * result + timeWindow;
        result = 31 * result + grade;
        // result = 31 * result + (cut ? 1 : 0);
        // result = 31 * result + (passCount != null ? (int)passCount.get() :
        // 0);
        return result;
    }

    @Override
    public boolean passCheck(Context context, DefaultNode node, int acquireCount, Object... args) {

        if (cut) {
            return false;
        }

        ClusterNode clusterNode = ClusterBuilderSlot.getClusterNode(this.getResource());
        if (clusterNode == null) {
            return true;
        }

        if (grade == RuleConstant.DEGRADE_GRADE_RT) {
            double rt = clusterNode.avgRt();
            if (rt < this.count) {
                return true;
            }

            // Sentinel will degrade the service only if count exceeds.
            if (passCount.incrementAndGet() < RT_MAX_EXCEED_N) {
                return true;
            }
        } else {
            double exception = clusterNode.exceptionQps();
            double success = clusterNode.successQps();
            if (success == 0) {
                return true;
            }

            if (exception / success < count) {
                return true;
            }
        }

        synchronized (lock) {
            if (!cut) {
                // Automatically degrade.
                cut = true;
                ResetTask resetTask = new ResetTask(this);
                pool.schedule(resetTask, timeWindow, TimeUnit.SECONDS);
            }

            return false;
        }
    }

    @Override
    public String toString() {
        return "DegradeRule{" +
                "resource=" + getResource() +
                ", grade=" + grade +
                ", count=" + count +
                ", limitApp=" + getLimitApp() +
                ", timeWindow=" + timeWindow +
                "}";
    }

    private static final class ResetTask implements Runnable {

        private DegradeRule rule;

        ResetTask(DegradeRule rule) {
            this.rule = rule;
        }

        @Override
        public void run() {
            rule.getPassCount().set(0);
            rule.setCut(false);
        }
    }
}

