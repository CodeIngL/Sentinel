package com.alibaba.csp.sentinel.slots.system;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author jialiang.linjl
 */
public class SystemBlockException extends BlockException {

    String resourceName;

    public String getResourceName() {
        return resourceName;
    }

    public SystemBlockException(String resourceName, String message, Throwable cause) {
        super(message, cause);
        this.resourceName = resourceName;
    }

    public SystemBlockException(String resourceName, String ruleLimitApp) {
        super(ruleLimitApp);
        this.resourceName = resourceName;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
