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

    /**
     * 用于构建处理其链{@link com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain}
     */
    public static final SlotsChainBuilder slotsChainbuilder = new DefaultSlotsChainBuilder();
    /**
     * 用于构建{@link com.alibaba.csp.sentinel.node.DefaultNode}
     */
    public static final NodeBuilder nodeBuilder = new DefaultNodeBuilder();
    /**
     * 全局的唯一的{@link Sph}
     */
    public static final Sph sph = new CtSph();

    static {
        // If init fails, the process will exit.
        // 如果init失败，则进程将退出。
        // 使用SPI机制
        InitExecutor.doInit();
    }

}
