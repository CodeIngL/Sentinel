package com.alibaba.csp.sentinel.transport.util;

import com.alibaba.csp.sentinel.command.CommandRequest;

/**
 * Util class for HTTP command center.
 *
 * @author Eric Zhao
 */
public final class HttpCommandUtils {

    public static final String REQUEST_TARGET = "command-target";

    public static String getTarget(CommandRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        return request.getMetadata().get(REQUEST_TARGET);
    }

    private HttpCommandUtils() {}
}
