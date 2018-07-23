package com.alibaba.csp.sentinel.transport.client;

import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;

/**
 * Basic interface for clients that sending commands.
 *
 * @author Eric Zhao
 */
public interface CommandClient {

    /**
     * Send a command to target destination.
     *
     * @param host    target host
     * @param port    target port
     * @param request command request
     * @return the response from target command server
     * @throws Exception when unexpected error occurs
     */
    CommandResponse sendCommand(String host, int port, CommandRequest request) throws Exception;
}
