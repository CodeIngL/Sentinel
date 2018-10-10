package com.alibaba.csp.sentinel.util

import com.alibaba.csp.sentinel.Entry
import com.alibaba.csp.sentinel.SphU
import com.alibaba.csp.sentinel.context.ContextUtil
import com.alibaba.csp.sentinel.slots.block.BlockException
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager

import static com.alibaba.csp.sentinel.context.ContextUtil.enter
import static com.alibaba.csp.sentinel.slots.block.RuleConstant.FLOW_GRADE_QPS
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule

/**
 cif
 *
 */
class Main {

    public static void main(String[] args) {
        def rule = new FlowRule();
        rule.resource="HelloWorld"; rule.grade= FLOW_GRADE_QPS; rule.count=20;
        FlowRuleManager.loadRules([rule]);
        while (true) {
            def entry = null;
            try {
                entry = SphU.entry("HelloWorld");
                println("hello world");
            } catch (BlockException e1) {
                println("block!");
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        }

        enter("codeL","app1");
        def entryOne = null;
        def entryTwo = null;
        try {
            entryOne = SphU.entry("HelloWorldOne");
            entryTwo = SphU.entry("HelloWorldTwo");
        } catch (BlockException e1) {
            println("block!");
        } finally {
            if (entryTwo != null) {
                entryTwo.exit();
            }
            if (entryOne != null) {
                entryOne.exit();
            }
        }

    }
}
