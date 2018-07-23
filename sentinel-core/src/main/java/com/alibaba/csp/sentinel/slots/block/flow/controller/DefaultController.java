package com.alibaba.csp.sentinel.slots.block.flow.controller;

import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.Controller;

/**
 * @author jialiang.linjl
 */
public class DefaultController implements Controller {

    double count = 0;
    int grade = 0;

    public DefaultController(double count, int grade) {
        super();
        this.count = count;
        this.grade = grade;
    }

    @Override
    public boolean canPass(Node node, int acquireCount) {
        int curCount = avgUsedTokens(node);
        if (curCount + acquireCount > count) {
            return false;
        }

        return true;
    }

    private int avgUsedTokens(Node node) {
        if (node == null) {
            return -1;
        }
        return grade == RuleConstant.FLOW_GRADE_THREAD ? node.curThreadNum() : (int)node.passQps();
    }

}
