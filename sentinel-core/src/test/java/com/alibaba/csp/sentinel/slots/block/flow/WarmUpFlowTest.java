package com.alibaba.csp.sentinel.slots.block.flow;

import java.util.Arrays;

import org.junit.Test;

import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;

/**
 * @author jialiang.linjl
 */
public class WarmUpFlowTest {

    @Test
    public void testWarmupFlowControl() {
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("testWarmupFlowControl");
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        flowRule.setCount(10);
        flowRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        flowRule.setWarmUpPeriodSec(10);
        flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);

        FlowRuleManager.loadRules(Arrays.asList(flowRule));

        //ContextUtil.enter(null);

        //when(flowRule.selectNodeByRequsterAndStrategy(null, null, null)).thenReturn(value)

        // flowRule.passCheck(null, DefaultNode, acquireCount, args)
        // when(leapArray.values()).thenReturn(new ArrayList<Window>() {{ add(windowWrap.value()); }});
        ContextUtil.enter("test");

        ContextUtil.exit();

    }

}
