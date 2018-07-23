package com.alibaba.csp.sentinel.slots.block;

/***
 * @author youji.zj
 * @author jialiang.linjl
 */
public class RuleConstant {

    public static final int FLOW_GRADE_THREAD = 0;
    public static final int FLOW_GRADE_QPS = 1;

    public static final int DEGRADE_GRADE_RT = 0;
    public static final int DEGRADE_GRADE_EXCEPTION = 1;

    public static final int WHILE = 0;
    public static final int BLACK = 1;

    public static final int STRATEGY_DIRECT = 0;
    public static final int STRATEGY_RELATE = 1;
    public static final int STRATEGY_CHAIN = 2;

    public static final int CONTROL_BEHAVIOR_DEFAULT = 0;
    public static final int CONTROL_BEHAVIOR_WARM_UP = 1;
    public static final int CONTROL_BEHAVIOR_RATE_LIMITER = 2;

}
