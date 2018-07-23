package com.alibaba.csp.sentinel.slots.block.degrade;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/***
 * @author youji.zj
 */
public class DegradeException extends BlockException {

    public DegradeException(String ruleLimitApp) {
        super(ruleLimitApp);
    }

    public DegradeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DegradeException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
