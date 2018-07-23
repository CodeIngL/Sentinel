package com.alibaba.csp.sentinel.command;

/**
 * Represent a handler that handles a {@link CommandRequest}.
 *
 * @author Eric Zhao
 */
public interface CommandHandler<R> {

    /**
     * Handle the given Courier command request.
     *
     * @param request the request to handle
     * @return the response
     */
    CommandResponse<R> handle(CommandRequest request);
}
