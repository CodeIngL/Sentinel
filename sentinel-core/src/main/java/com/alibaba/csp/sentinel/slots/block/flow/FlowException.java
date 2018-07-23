package com.alibaba.csp.sentinel.slots.block.flow;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/***
 * @author youji.zj
 */
public class FlowException extends BlockException {

    public FlowException(String ruleLimitApp) {

        super(ruleLimitApp);
    }

    public FlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
