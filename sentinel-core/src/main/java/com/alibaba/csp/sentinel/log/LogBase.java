package com.alibaba.csp.sentinel.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.csp.sentinel.util.PidUtil;

/**
 * @author leyou
 */
public class LogBase {
    public static final String LOG_CHARSET = "utf-8";
    private static final String DIR_NAME = "logs" + File.separator + "csp";
    private static final String USER_HOME = "user.home";
    private static String logBaseDir;

    static {
        String userHome = System.getProperty(USER_HOME);
        setLogBaseDir(userHome);
    }

    /**
     * Get log file base directory path, the returned path is guaranteed end with {@link File#separator}
     *
     * @return log file base directory path.
     */
    public static String getLogBaseDir() {
        return logBaseDir;
    }

    /**
     * Change log dir, the dir will be created if not exits
     *
     * @param baseDir
     */
    protected static void setLogBaseDir(String baseDir) {
        if (!baseDir.endsWith(File.separator)) {
            baseDir += File.separator;
        }
        String path = baseDir + DIR_NAME + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        logBaseDir = path;
    }

    protected static Handler makeLogger(String logName, Logger heliumRecordLog) {
        CspFormatter formatter = new CspFormatter();
        String fileName = LogBase.getLogBaseDir() + logName + ".pid" + PidUtil.getPid();
        Handler handler = null;
        try {
            handler = new DateFileLogHandler(fileName + ".%d", 1024 * 1024 * 200, 1, true);
            handler.setFormatter(formatter);
            handler.setEncoding(LOG_CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (handler != null) {
            LoggerUtils.disableOtherHandlers(heliumRecordLog, handler);
        }
        heliumRecordLog.setLevel(Level.ALL);
        return handler;
    }
}
