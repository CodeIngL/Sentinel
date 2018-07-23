package com.alibaba.csp.sentinel.log;

import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Util class for logger.
 */
class LoggerUtils {

    /**
     * Remove all current handlers from the logger and attach it with the given log handler.
     *
     * @param logger  logger
     * @param handler the log handler
     */
    static void disableOtherHandlers(Logger logger, Handler handler) {
        if (logger == null) {
            return;
        }

        synchronized (logger) {
            Handler[] handlers = logger.getHandlers();
            if (handlers == null) {
                return;
            }
            if (handlers.length == 1 && handlers[0].equals(handler)) {
                return;
            }

            logger.setUseParentHandlers(false);
            // Remove all current handlers.
            for (Handler h : handlers) {
                logger.removeHandler(h);
            }
            // Attach the given handler.
            logger.addHandler(handler);
        }
    }
}
