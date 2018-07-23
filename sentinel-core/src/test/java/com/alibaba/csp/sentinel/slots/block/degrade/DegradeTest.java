package com.alibaba.csp.sentinel.slots.block.degrade;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.StringResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

/**
 * @author jialiang.linjl
 */
public class DegradeTest {

    @Test
    public void testAverageRtDegrade() throws InterruptedException {
        String key = "test_degrade_average_rt";
        ClusterNode cn = mock(ClusterNode.class);
        ClusterBuilderSlot.getClusterNodeMap().put(new StringResourceWrapper(key, EntryType.IN), cn);

        Context context = mock(Context.class);
        DefaultNode node = mock(DefaultNode.class);
        when(node.getClusterNode()).thenReturn(cn);
        when(cn.avgRt()).thenReturn(2L);

        DegradeRule rule = new DegradeRule();
        rule.setCount(1);
        rule.setResource(key);
        rule.setTimeWindow(5);

        for (int i = 0; i < 4; i++) {
            assertTrue(rule.passCheck(context, node, 1));
        }

        // The third time will fail.
        assertFalse(rule.passCheck(context, node, 1));
        assertFalse(rule.passCheck(context, node, 1));

        // Restore.
        TimeUnit.SECONDS.sleep(6);
        assertTrue(rule.passCheck(context, node, 1));
    }

    @Test
    public void testExceptionRatioModeDegrade() throws Throwable {
        String key = "test_degrade_exception_ratio";
        ClusterNode cn = mock(ClusterNode.class);
        when(cn.exceptionQps()).thenReturn(2L);
        ClusterBuilderSlot.getClusterNodeMap().put(new StringResourceWrapper(key, EntryType.IN), cn);

        Context context = mock(Context.class);
        DefaultNode node = mock(DefaultNode.class);
        when(node.getClusterNode()).thenReturn(cn);

        DegradeRule rule = new DegradeRule();
        rule.setCount(0.5);
        rule.setResource(key);
        rule.setTimeWindow(5);
        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION);

        when(cn.successQps()).thenReturn(4L);

        // Will fail.
        assertFalse(rule.passCheck(context, node, 1));

        // Restore from the degrade timeout.
        TimeUnit.SECONDS.sleep(6);

        when(cn.successQps()).thenReturn(7L);
        // Will pass.
        assertTrue(rule.passCheck(context, node, 1));
    }

}
