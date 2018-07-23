package com.alibaba.csp.sentinel.slots.block;

/**
 * A {@link RuntimeException} marks sentinel RPC exception. The stack trace
 * is removed for high performance.
 *
 * @author leyou
 */
public class SentinelRpcException extends RuntimeException {

    public SentinelRpcException(String msg) {
        super(msg);
    }

    public SentinelRpcException(Throwable e) {
        super(e);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
