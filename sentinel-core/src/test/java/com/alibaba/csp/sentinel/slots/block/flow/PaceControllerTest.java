package com.alibaba.csp.sentinel.slots.block.flow;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.block.flow.controller.PaceController;

/**
 * @author jialiang.linjl
 */
public class PaceControllerTest {

    @Test
    public void testPaceController_normal() throws InterruptedException {
        PaceController paceController = new PaceController(500, 10d);
        Node node = mock(Node.class);

        long start = TimeUtil.currentTimeMillis();
        for (int i = 0; i < 6; i++) {
            assertTrue(paceController.canPass(node, 1));
        }
        long end = TimeUtil.currentTimeMillis();
        assertTrue((end - start) > 400);
    }

    @Test
    public void testPaceController_timeout() throws InterruptedException {
        final PaceController paceController = new PaceController(500, 10d);
        final Node node = mock(Node.class);

        final AtomicInteger passcount = new AtomicInteger();
        final AtomicInteger blockcount = new AtomicInteger();
        final CountDownLatch countDown = new CountDownLatch(1);

        final AtomicInteger done = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean pass = paceController.canPass(node, 1);

                    if (pass == true) {
                        passcount.incrementAndGet();
                    } else {
                        blockcount.incrementAndGet();
                    }
                    done.incrementAndGet();

                    if (done.get() >= 10) {
                        countDown.countDown();
                    }
                }

            }, "Thread " + i);
            thread.start();
        }

        countDown.await();
        System.out.println("pass:" + passcount.get());
        System.out.println("block" + blockcount.get());
        System.out.println("done" + done.get());
        assertTrue(blockcount.get() > 0);

    }

}
