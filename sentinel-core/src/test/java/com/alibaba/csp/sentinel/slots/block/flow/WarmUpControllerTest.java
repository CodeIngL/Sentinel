package com.alibaba.csp.sentinel.slots.block.flow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.block.flow.controller.WarmUpController;

/**
 * @author jialiang.linjl
 */
public class WarmUpControllerTest {

    @Test
    public void testWarmUp() throws InterruptedException {
        WarmUpController warmupController = new WarmUpController(10, 10, 3);

        Node node = mock(Node.class);

        when(node.passQps()).thenReturn(8L);
        when(node.previousPassQps()).thenReturn(1L);

        assertFalse(warmupController.canPass(node, 1));

        when(node.passQps()).thenReturn(1L);
        when(node.previousPassQps()).thenReturn(1L);

        assertTrue(warmupController.canPass(node, 1));

        when(node.previousPassQps()).thenReturn(10L);

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            warmupController.canPass(node, 1);
        }
        when(node.passQps()).thenReturn(8L);
        assertTrue(warmupController.canPass(node, 1));

        when(node.passQps()).thenReturn(10L);
        assertFalse(warmupController.canPass(node, 1));
    }
}
