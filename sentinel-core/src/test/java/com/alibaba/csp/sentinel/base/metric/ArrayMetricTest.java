package com.alibaba.csp.sentinel.base.metric;

import java.util.ArrayList;

import org.junit.Test;

import com.alibaba.csp.sentinel.slots.statistic.base.Window;
import com.alibaba.csp.sentinel.slots.statistic.base.WindowWrap;
import com.alibaba.csp.sentinel.slots.statistic.metric.ArrayMetric;
import com.alibaba.csp.sentinel.slots.statistic.metric.WindowLeapArray;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Test cases for {@link ArrayMetric}.
 *
 * @author Eric Zhao
 */
public class ArrayMetricTest {

    private final int windowLengthInMs = 500;
    private final int intervalInSec = 1;

    @Test
    public void testOperateArrayMetric() {
        WindowLeapArray leapArray = mock(WindowLeapArray.class);
        final WindowWrap<Window> windowWrap = new WindowWrap<Window>(windowLengthInMs, 0, new Window());
        when(leapArray.currentWindow()).thenReturn(windowWrap);
        when(leapArray.values()).thenReturn(new ArrayList<Window>() {{ add(windowWrap.value()); }});

        ArrayMetric metric = new ArrayMetric(leapArray);

        final int expectedPass = 9;
        final int expectedBlock = 2;
        final int expectedSuccess = 9;
        final int expectedException = 6;
        final int expectedRt = 21;

        metric.addRT(expectedRt);
        for (int i = 0; i < expectedPass; i++) {
            metric.addPass();
        }
        for (int i = 0; i < expectedBlock; i++) {
            metric.addBlock();
        }
        for (int i = 0; i < expectedSuccess; i++) {
            metric.addSuccess();
        }
        for (int i = 0; i < expectedException; i++) {
            metric.addException();
        }

        assertEquals(expectedPass, metric.pass());
        assertEquals(expectedBlock, metric.block());
        assertEquals(expectedSuccess, metric.success());
        assertEquals(expectedException, metric.exception());
        assertEquals(expectedRt, metric.rt());
    }
}