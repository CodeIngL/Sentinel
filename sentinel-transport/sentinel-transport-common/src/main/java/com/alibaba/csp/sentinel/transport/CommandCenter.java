package com.alibaba.csp.sentinel.transport;

/**
 * @author Eric Zhao
 */
public interface CommandCenter {

    /**
     * Prepare and init for the command center (e.g. register commands).
     * This will be executed before starting.
     *
     * @throws Exception if error occurs
     */
    void beforeStart() throws Exception;

    /**
     * Start the command center in the background.
     * This method should NOT block.
     *
     * @throws Exception if error occurs
     */
    void start() throws Exception;

    /**
     * Stop the command center and do cleanup.
     *
     * @throws Exception if error occurs
     */
    void stop() throws Exception;
}
