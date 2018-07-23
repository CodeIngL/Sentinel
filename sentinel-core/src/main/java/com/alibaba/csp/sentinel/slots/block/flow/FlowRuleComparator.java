package com.alibaba.csp.sentinel.slots.block.flow;

import java.util.Comparator;

public class FlowRuleComparator implements Comparator<FlowRule> {

    @Override
    public int compare(FlowRule o1, FlowRule o2) {

        if (o1.getLimitApp() == null) {
            return 0;
        }

        if (o1.getLimitApp().equals(o2.getLimitApp())) {
            return 0;
        }

        if (FlowRule.DEFAULT.equals(o1.getLimitApp())) {
            return 1;
        } else if (FlowRule.DEFAULT.equals(o2.getLimitApp())) {
            return -1;
        } else {
            return 0;
        }
    }

}
