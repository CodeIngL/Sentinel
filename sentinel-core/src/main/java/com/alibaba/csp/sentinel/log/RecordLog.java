package com.alibaba.csp.sentinel.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * The basic logger for vital events.
 *
 * @author youji.zj
 */
public class RecordLog extends LogBase {
    private static final Logger heliumRecordLog = Logger.getLogger("cspRecordLog");
    private static final String FILE_NAME = "record.log";
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

    public static void info(String detail) {
        LoggerUtils.disableOtherHandlers(heliumRecordLog, logHandler);
        heliumRecordLog.log(Level.INFO, detail);
    }

    public static void info(String detail, Throwable e) {
        LoggerUtils.disableOtherHandlers(heliumRecordLog, logHandler);
        heliumRecordLog.log(Level.INFO, detail, e);
    }
}
