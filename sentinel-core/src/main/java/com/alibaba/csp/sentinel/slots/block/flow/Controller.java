package com.alibaba.csp.sentinel.slots.block.flow;

import com.alibaba.csp.sentinel.node.Node;

/**
 * 控制器，规则控制器
 * @author jialiang.linjl
 */
public interface Controller {

    /**
     * 是否能通过
     * @param node
     * @param acquireCount
     * @return
     */
    boolean canPass(Node node, int acquireCount);

}
