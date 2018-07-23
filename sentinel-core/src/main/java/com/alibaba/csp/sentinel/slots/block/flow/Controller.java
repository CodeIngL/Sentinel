package com.alibaba.csp.sentinel.slots.block.flow;

import com.alibaba.csp.sentinel.node.Node;

/**
 * @author jialiang.linjl
 */
public interface Controller {

    boolean canPass(Node node, int acquireCount);

}
