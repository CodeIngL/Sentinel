package com.alibaba.csp.sentinel.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger for command center.
 */
public class CommandCenterLog extends LogBase {

    private static final Logger heliumRecordLog = Logger.getLogger("cspMetricLog");
    private static final String FILE_NAME = "metricStat.log";
    private static Handler logHandler = null;

    static {
        logHandler = makeLogger(FILE_NAME, heliumRecordLog);
    }

    /**
     * Change log dir, the dir will be created if not exits
     */
    public static void resetLogBaseDir(String baseDir) {
        setLogBaseDir(baseDir);
        logHandler = makeLogger(FILE_NAME, heliumRecordLog);
    }

    public static void info(String msg) {
        LoggerUtils.disableOtherHandlers(heliumRecordLog, logHandler);
        heliumRecordLog.log(Level.INFO, msg);
    }

    public static void info(String msg, Throwable e) {
        LoggerUtils.disableOtherHandlers(heliumRecordLog, logHandler);
        heliumRecordLog.log(Level.INFO, msg, e);
    }

    public static void warn(String msg, Throwable e) {
        LoggerUtils.disableOtherHandlers(heliumRecordLog, logHandler);
        heliumRecordLog.log(Level.WARNING, msg, e);
    }
}
