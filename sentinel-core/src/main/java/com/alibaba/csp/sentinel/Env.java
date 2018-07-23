package com.alibaba.csp.sentinel;

import com.alibaba.csp.sentinel.init.InitExecutor;
import com.alibaba.csp.sentinel.node.DefaultNodeBuilder;
import com.alibaba.csp.sentinel.node.NodeBuilder;
import com.alibaba.csp.sentinel.slots.DefaultSlotsChainBuilder;
import com.alibaba.csp.sentinel.slots.SlotsChainBuilder;

/**
 * @author jialiang.linjl
 */
public class Env {

    public static final SlotsChainBuilder slotsChainbuilder = new DefaultSlotsChainBuilder();
    public static final NodeBuilder nodeBuilder = new DefaultNodeBuilder();
    public static final Sph sph = new CtSph();

    static {
        // If init fails, the process will exit.
        InitExecutor.doInit();
    }

}
