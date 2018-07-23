package com.alibaba.csp.sentinel;

import com.alibaba.csp.sentinel.log.RecordLog;

import org.junit.Test;

/**
 * @author xuyue
 */
public class RecordLogTest {

    @Test
    public void testLogException() {
        Exception e = new Exception("ex");
        RecordLog.info("Error", e);
    }

    @Test
    public void testLogRolling() {
        int count = 1000;
        while (--count > 0) {
            RecordLog.info("Count " + count);
        }
    }

}