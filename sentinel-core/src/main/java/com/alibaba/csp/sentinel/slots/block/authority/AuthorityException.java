package com.alibaba.csp.sentinel.slots.block.authority;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/***
 *
 * @author youji.zj
 */
public class AuthorityException extends BlockException {

    public AuthorityException(String ruleLimitApp) {
        super(ruleLimitApp);
    }

    public AuthorityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorityException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
