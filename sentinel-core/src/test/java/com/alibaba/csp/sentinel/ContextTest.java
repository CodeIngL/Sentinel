package com.alibaba.csp.sentinel;

import org.junit.Test;

import com.alibaba.csp.sentinel.context.ContextUtil;

/**
 * @author jialiang.linjl
 */
public class ContextTest {

    @Test
    public void testEnterContext() {
        // TODO: rewrite this unit test
        ContextUtil.enter("entry", "origin");

        ContextUtil.exit();
    }

}
