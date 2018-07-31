package com.alibaba.csp.sentinel.slots.block.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.metric.MetricTimerListener;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.property.PropertyListener;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.controller.DefaultController;
import com.alibaba.csp.sentinel.slots.block.flow.controller.PaceController;
import com.alibaba.csp.sentinel.slots.block.flow.controller.WarmUpController;

/**
 * <p>
 * One resources can have multiple rules. And these rules take effects in the
 * following order:
 * <ol>
 * <li>requests from specified caller</li>
 * <li>no specified caller</li>
 * </ol>
 * </p>
 * <p>
 * <p>
 * 一个resources可以有多个rules。 这些规则按以下顺序生效：
 * <ol>
 * <li>来自指定调用者的请求</li>
 * <li>没有指定的调用者</li>
 * </ol>
 * </p>
 *
 * @author jialiang.linjl
 */

public class FlowRuleManager {

    /**
     * 规则存储结构
     */
    private static final Map<String, List<FlowRule>> flowRules = new ConcurrentHashMap<String, List<FlowRule>>();
    //执行器
    private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    //监听器
    private final static FlowPropertyListener listener = new FlowPropertyListener();
    //监听器
    private static SentinelProperty<List<FlowRule>> currentProperty = new DynamicSentinelProperty<List<FlowRule>>();

    static {
        currentProperty.addListener(listener);
        scheduler.scheduleAtFixedRate(new MetricTimerListener(), 0, 1, TimeUnit.SECONDS);
    }

    public static void register2Property(SentinelProperty<List<FlowRule>> property) {
        synchronized (listener) {
            currentProperty.removeListener(listener);
            property.addListener(listener);
            currentProperty = property;
        }
    }

    /**
     * Get a copy of the rules.
     * <p>
     * 获取rules的副本。
     * </p>
     * <p>
     * notice:仅仅是为了展现功能而使用
     * </p>
     *
     * @return a new copy of the rules.
     */
    public static List<FlowRule> getRules() {
        List<FlowRule> rules = new ArrayList<FlowRule>();
        if (flowRules == null) {
            return rules;
        }
        for (Map.Entry<String, List<FlowRule>> entry : flowRules.entrySet()) {
            rules.addAll(entry.getValue());
        }
        return rules;
    }

    /**
     * Load {@link FlowRule}s, former rules will be replaced.
     * <p>
     * 加载{@link FlowRule},先前的Rules将被替代
     * </p>
     *
     * @param rules new rules to load.
     */
    public static void loadRules(List<FlowRule> rules) {
        currentProperty.updateValue(rules);
    }

    /**
     * 对list进行分类，转换为map形式
     *
     * @param list
     * @return
     */
    private static Map<String, List<FlowRule>> loadFlowConf(List<FlowRule> list) {
        Map<String, List<FlowRule>> newRuleMap = new ConcurrentHashMap<String, List<FlowRule>>();

        if (list == null) {
            return newRuleMap;
        }

        //分类
        for (FlowRule rule : list) {
            if (StringUtil.isBlank(rule.getLimitApp())) {
                rule.setLimitApp(FlowRule.DEFAULT);
            }

            //构建控制器
            Controller rater = new DefaultController(rule.getCount(), rule.getGrade());
            if (rule.getGrade() == RuleConstant.FLOW_GRADE_QPS //QPS
                    && rule.getControlBehavior() == RuleConstant.CONTROL_BEHAVIOR_WARM_UP //WARM_UP
                    && rule.getWarmUpPeriodSec() > 0) {
                rater = new WarmUpController(rule.getCount(), rule.getWarmUpPeriodSec(), ColdFactorProperty.coldFactor);

            } else if (rule.getGrade() == RuleConstant.FLOW_GRADE_QPS //QPS
                    && rule.getControlBehavior() == RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER //速率限制
                    && rule.getMaxQueueingTimeMs() > 0) {
                rater = new PaceController(rule.getMaxQueueingTimeMs(), rule.getCount());
            }
            rule.setRater(rater);

            //根据规则的id进行分类。最终生成map
            String identity = rule.getResource();
            List<FlowRule> ruleM = newRuleMap.get(identity);

            if (ruleM == null) {
                ruleM = new ArrayList<FlowRule>();
                newRuleMap.put(identity, ruleM);
            }

            ruleM.add(rule);

        }
        return newRuleMap;
    }

    public static void checkFlow(ResourceWrapper resource, Context context, DefaultNode node, int count)
            throws BlockException {
        if (flowRules != null) {
            List<FlowRule> rules = flowRules.get(resource.getName());
            if (rules != null) {
                for (FlowRule rule : rules) {
                    if (!rule.passCheck(context, node, count)) {
                        throw new FlowException(rule.getLimitApp());
                    }
                }
            }
        }
    }

    public static boolean hasConfig(String resource) {
        return flowRules.containsKey(resource);
    }

    public static boolean isOtherOrigin(String origin, String resourceName) {
        if (StringUtil.isEmpty(origin)) {
            return false;
        }

        if (flowRules != null) {
            List<FlowRule> rules = flowRules.get(resourceName);

            if (rules != null) {
                for (FlowRule rule : rules) {
                    if (origin.equals(rule.getLimitApp())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * FlowRule搭配属性监听器实现
     */
    private static final class FlowPropertyListener implements PropertyListener<List<FlowRule>> {

        /**
         * 规则变化时进行更新，全量更新
         *
         * @param value
         */
        @Override
        public void configUpdate(List<FlowRule> value) {
            Map<String, List<FlowRule>> rules = loadFlowConf(value);
            if (rules != null) {
                flowRules.clear();
                flowRules.putAll(rules);
            }
            RecordLog.info("receive flow config: " + flowRules);
        }

        /**
         * 加入到属性器时触发的行为，全量刷新
         *
         * @param conf
         */
        @Override
        public void configLoad(List<FlowRule> conf) {
            Map<String, List<FlowRule>> rules = loadFlowConf(conf);
            if (rules != null) {
                flowRules.clear();
                flowRules.putAll(rules);
            }
            RecordLog.info("load flow config: " + flowRules);
        }

    }

}
