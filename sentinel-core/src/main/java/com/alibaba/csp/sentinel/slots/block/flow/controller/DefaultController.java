package com.alibaba.csp.sentinel.slots.block.flow.controller;

import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.Controller;

/**
 * 默认的控制器
 *
 * @author jialiang.linjl
 */
public class DefaultController implements Controller {

    //数量指标
    double count = 0;
    //种类指标：0是线程，1是QPS
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

    /**
     *
     * @param node
     * @return
     */
    private int avgUsedTokens(Node node) {
        if (node == null) {
            return -1;
        }
        return grade == RuleConstant.FLOW_GRADE_THREAD ? node.curThreadNum() : (int) node.passQps();
    }

}
