package com.alibaba.csp.sentinel;

import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.EntranceNode;
import com.alibaba.csp.sentinel.slotchain.StringResourceWrapper;
import com.alibaba.csp.sentinel.slots.system.SystemRule;

/**
 * @author qinan.qn
 * @author youji.zj
 * @author jialiang.linjl
 */
public class Constants {

    public final static int MAX_CONTEXT_NAME_SIZE = 2000;
    public final static int MAX_SLOT_CHAIN_SIZE = 6000;
    public final static String ROOT_ID = "machine-root";
    public final static String CONTEXT_DEFAULT_NAME = "default_context_name";

    //应用的root节点
    public final static DefaultNode ROOT = new EntranceNode(new StringResourceWrapper(ROOT_ID, EntryType.IN),
        Env.nodeBuilder.buildClusterNode());

    /**
     * statistics for {@link SystemRule} checking.
     */
    public final static ClusterNode ENTRY_NODE = new ClusterNode();

    /**
     * 超过这个时间的请求不作为平均时间计算
     */
    public final static int TIME_DROP_VALVE = 4900;

    /*** 框架功能打开或者关闭的开关 ***/
    public static volatile boolean ON = true;

}