package com.alibaba.csp.sentinel.slots.block.system;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.StringResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;

/**
 * @author jialiang.linjl
 */
public class SystemRuleTest {

    @Test
    public void testSystemRule_load() {
        SystemRule systemRule = new SystemRule();

        systemRule.setAvgRt(4000L);

        SystemRuleManager.loadRules(Collections.singletonList(systemRule));
    }

    @Test
    public void testSystemRule_avgRt() throws BlockException {

        SystemRule systemRule = new SystemRule();

        systemRule.setAvgRt(4L);

        Context context = mock(Context.class);
        DefaultNode node = mock(DefaultNode.class);
        ClusterNode cn = mock(ClusterNode.class);

        when(context.getOrigin()).thenReturn("");
        when(node.getClusterNode()).thenReturn(cn);

    }

}
