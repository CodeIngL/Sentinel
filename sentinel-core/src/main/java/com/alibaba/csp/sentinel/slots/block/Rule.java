package com.alibaba.csp.sentinel.slots.block;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;

/**
 * Base interface of all rules.
 *
 * @author youji.zj
 */
public interface Rule {

    /**
     * Check whether current statistical indicators meet this rule, which means not exceeding any threshold.
     *
     * @param context current {@link Context}
     * @param node    current {@link com.alibaba.csp.sentinel.node.Node}
     * @param count   tokens needed.
     * @param args    arguments of the original invocation.
     * @return If current statistical indicators not exceeding any threshold return true, otherwise return false.
     */
    boolean passCheck(Context context, DefaultNode node, int count, Object... args);

}
