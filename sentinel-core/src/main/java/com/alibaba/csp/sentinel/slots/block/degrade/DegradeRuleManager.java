package com.alibaba.csp.sentinel.slots.block.degrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.property.PropertyListener;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

/***
 * @author youji.zj
 * @author jialiang.linjl
 */
public class DegradeRuleManager {

    private static volatile Map<String, List<DegradeRule>> degradeRules
        = new ConcurrentHashMap<String, List<DegradeRule>>();

    final static RulePropertyListener listener = new RulePropertyListener();
    private static SentinelProperty<List<DegradeRule>> currentProperty
        = new DynamicSentinelProperty<List<DegradeRule>>();

    static {
        currentProperty.addListener(listener);
    }

    public static void register2Property(SentinelProperty<List<DegradeRule>> property) {
        synchronized (listener) {
            currentProperty.removeListener(listener);
            property.addListener(listener);
            currentProperty = property;
        }
    }

    public static void checkDegrade(ResourceWrapper resource, Context context, DefaultNode node, int count)
        throws BlockException {
        if (degradeRules == null) {
            return;
        }

        List<DegradeRule> rules = degradeRules.get(resource.getName());
        if (rules == null) {
            return;
        }

        for (DegradeRule rule : rules) {
            if (!rule.passCheck(context, node, count)) {
                throw new DegradeException(rule.getLimitApp());
            }
        }
    }

    public static boolean hasConfig(String resource) {
        return degradeRules.containsKey(resource);
    }

    /**
     * Get a copy of the rules.
     *
     * @return a new copy of the rules.
     */
    public static List<DegradeRule> getRules() {
        List<DegradeRule> rules = new ArrayList<DegradeRule>();
        if (degradeRules == null) {
            return rules;
        }
        for (Map.Entry<String, List<DegradeRule>> entry : degradeRules.entrySet()) {
            rules.addAll(entry.getValue());
        }
        return rules;
    }

    /**
     * Load {@link DegradeRule}s, former rules will be replaced.
     *
     * @param rules new rules to load.
     */
    public static void loadRules(List<DegradeRule> rules) {
        try {
            currentProperty.updateValue(rules);
        } catch (Throwable e) {
            RecordLog.info(e.getMessage(), e);
        }
    }

    private static class RulePropertyListener implements PropertyListener<List<DegradeRule>> {

        @Override
        public void configUpdate(List<DegradeRule> conf) {
            Map<String, List<DegradeRule>> rules = loadDegradeConf(conf);
            if (rules != null) {
                degradeRules.clear();
                degradeRules.putAll(rules);
            }
            RecordLog.info("receive degrade config: " + degradeRules);
        }

        @Override
        public void configLoad(List<DegradeRule> conf) {
            Map<String, List<DegradeRule>> rules = loadDegradeConf(conf);
            if (rules != null) {
                degradeRules.clear();
                degradeRules.putAll(rules);
            }
            RecordLog.info("init degrade config: " + degradeRules);
        }

        private Map<String, List<DegradeRule>> loadDegradeConf(List<DegradeRule> list) {
            if (list == null) {
                return null;
            }
            Map<String, List<DegradeRule>> newRuleMap = new ConcurrentHashMap<String, List<DegradeRule>>();

            for (DegradeRule rule : list) {
                if (StringUtil.isBlank(rule.getLimitApp())) {
                    rule.setLimitApp(FlowRule.DEFAULT);
                }

                String identity = rule.getResource();
                List<DegradeRule> ruleM = newRuleMap.get(identity);
                if (ruleM == null) {
                    ruleM = new ArrayList<DegradeRule>();
                    newRuleMap.put(identity, ruleM);
                }
                ruleM.add(rule);
            }

            return newRuleMap;
        }

    }

}
