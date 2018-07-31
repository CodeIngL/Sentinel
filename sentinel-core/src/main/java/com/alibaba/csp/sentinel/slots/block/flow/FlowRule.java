package com.alibaba.csp.sentinel.slots.block.flow;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

/***
 * <p>
 *     Each flow rule is mainly composed of three factors: <strong>grade</strong>,
 * <strong>strategy</strong> and <strong>controlBehavior</strong>.
 * </p>
 * <ul>
 *     <li>The {@link #grade} represents the threshold type of flow control (by QPS or thread count).</li>
 *     <li>The {@link #strategy} represents the strategy based on invocation relation.</li>
 *     <li>The {@link #controlBehavior} represents the QPS shaping behavior (actions on incoming request when QPS
 *     exceeds the threshold).</li>
 * </ul>
 *
 * <p>
 *     每个流规则主要由三个因素组成：等级，策略和控制行为。
 * </p>
 * <ul>
 *     <li>The {@link #grade} 流量控制的阈值类型（通过QPS或线程计数）。</li>
 *     <li>The {@link #strategy} 代表了基于调用关系的策略。</li>
 *     <li>The {@link #controlBehavior} 表示QPS整形行为（当QPS超过阈值时对传入请求的操作）。</li>
 * </ul>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class FlowRule extends AbstractRule {

    public static final String DEFAULT = "default";
    public static final String OTHER = "other";

    /**
     * The threshold type of flow control (0: thread count, 1: QPS).
     */
    private int grade;

    private double count;

    /**
     * 0为直接限流;1为关联限流;2为链路限流
     */
    private int strategy;

    private String refResource;

    /**
     * Rate limiter control behavior.
     * 0. default, 1. warm up, 2. rate limiter
     */
    private int controlBehavior = RuleConstant.CONTROL_BEHAVIOR_DEFAULT;

    private int warmUpPeriodSec = 10;

    /**
     * Max queueing time in rate limiter behavior.
     */
    private int maxQueueingTimeMs = 500;

    private Controller controller;

    public int getControlBehavior() {
        return controlBehavior;
    }

    public FlowRule setControlBehavior(int controlBehavior) {
        this.controlBehavior = controlBehavior;
        return this;
    }

    public int getMaxQueueingTimeMs() {
        return maxQueueingTimeMs;
    }

    public FlowRule setMaxQueueingTimeMs(int maxQueueingTimeMs) {
        this.maxQueueingTimeMs = maxQueueingTimeMs;
        return this;
    }

    public FlowRule setRater(Controller rater) {
        this.controller = rater;
        return this;
    }

    public int getWarmUpPeriodSec() {
        return warmUpPeriodSec;
    }

    public FlowRule setWarmUpPeriodSec(int warmUpPeriodSec) {
        this.warmUpPeriodSec = warmUpPeriodSec;
        return this;
    }

    public int getGrade() {
        return grade;
    }

    public FlowRule setGrade(int grade) {
        this.grade = grade;
        return this;
    }

    public double getCount() {
        return count;
    }

    public FlowRule setCount(double count) {
        this.count = count;
        return this;
    }

    public int getStrategy() {
        return strategy;
    }

    public FlowRule setStrategy(int strategy) {
        this.strategy = strategy;
        return this;
    }

    public String getRefResource() {
        return refResource;
    }

    public FlowRule setRefResource(String refResource) {
        this.refResource = refResource;
        return this;
    }

    @Override
    public boolean passCheck(Context context, DefaultNode node, int acquireCount, Object... args) {
        String limitApp = this.getLimitApp();
        if (limitApp == null) {
            return true;
        }

        String origin = context.getOrigin();
        Node selectedNode = selectNodeByRequesterAndStrategy(origin, context, node);
        if (selectedNode == null) {
            return true;
        }

        return controller.canPass(selectedNode, acquireCount);
    }

    private Node selectNodeByRequesterAndStrategy(String origin, Context context, DefaultNode node) {
        // The limit app should not be empty.
        String limitApp = this.getLimitApp();

        if (limitApp.equals(origin)) {
            if (strategy == RuleConstant.STRATEGY_DIRECT) {
                return context.getOriginNode();
            }

            String refResource = this.getRefResource();
            if (StringUtil.isEmpty(refResource)) {
                return null;
            }

            if (strategy == RuleConstant.STRATEGY_RELATE) {
                return ClusterBuilderSlot.getClusterNode(refResource);
            }

            if (strategy == RuleConstant.STRATEGY_CHAIN) {
                if (!refResource.equals(context.getName())) {
                    return null;
                }
                return node;
            }

        } else if (limitApp.equals(DEFAULT)) {
            if (strategy == RuleConstant.STRATEGY_DIRECT) {
                return node.getClusterNode();
            }
            String refResource = this.getRefResource();
            if (StringUtil.isEmpty(refResource)) {
                return null;
            }

            if (strategy == RuleConstant.STRATEGY_RELATE) {
                return ClusterBuilderSlot.getClusterNode(refResource);
            }

            if (strategy == RuleConstant.STRATEGY_CHAIN) {
                if (!refResource.equals(context.getName())) {
                    return null;
                }
                return node;
            }

        } else if (limitApp.equals(OTHER) && FlowRuleManager.isOtherOrigin(origin, getResource())) {
            if (strategy == RuleConstant.STRATEGY_DIRECT) {
                return context.getOriginNode();
            }

            String refResource = this.getRefResource();
            if (StringUtil.isEmpty(refResource)) {
                return null;
            }
            if (strategy == RuleConstant.STRATEGY_RELATE) {
                return ClusterBuilderSlot.getClusterNode(refResource);
            }

            if (strategy == RuleConstant.STRATEGY_CHAIN) {
                if (!refResource.equals(context.getName())) {
                    return null;
                }
                if (node != null) {
                    return node;
                }
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlowRule)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        FlowRule flowRule = (FlowRule) o;

        if (grade != flowRule.grade) {
            return false;
        }
        if (Double.compare(flowRule.count, count) != 0) {
            return false;
        }
        if (strategy != flowRule.strategy) {
            return false;
        }
        if (refResource != null ? !refResource.equals(flowRule.refResource) : flowRule.refResource != null) {
            return false;
        }
        if (this.controlBehavior != flowRule.controlBehavior) {
            return false;
        }

        if (warmUpPeriodSec != flowRule.warmUpPeriodSec) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + grade;
        temp = Double.doubleToLongBits(count);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + strategy;
        result = 31 * result + (refResource != null ? refResource.hashCode() : 0);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + warmUpPeriodSec;
        result = 31 * result + controlBehavior;
        return result;
    }

    @Override
    public String toString() {
        return "FlowRule{" +
                "resource=" + getResource() +
                ", limitApp=" + getLimitApp() +
                ", grade=" + grade +
                ", count=" + count +
                ", strategy=" + strategy +
                ", refResource=" + refResource +
                ", controlBehavior=" + controlBehavior +
                ", warmUpPeriodSec=" + warmUpPeriodSec +
                ", maxQueueingTimeMs=" + maxQueueingTimeMs +
                ", controller=" + controller +
                "}";
    }
}
