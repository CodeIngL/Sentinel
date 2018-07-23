package com.alibaba.csp.sentinel.slots.logger;

import java.io.File;

import com.alibaba.csp.sentinel.eagleeye.EagleEye;
import com.alibaba.csp.sentinel.eagleeye.StatLogger;

public class EagleEyeLogUtil {

    private static final String DIR_NAME = "csp";
    private static final String FILE_NAME = "sentinel-block.log";

    private static StatLogger statLogger;

    static {
        String path = DIR_NAME + File.separator + FILE_NAME;

        statLogger = EagleEye.statLoggerBuilder("sentinel-block-record")
            .intervalSeconds(1)
            .entryDelimiter('|')
            .keyDelimiter(',')
            .valueDelimiter(',')
            .maxEntryCount(6000)
            .baseLogFilePath(path)
            .maxFileSizeMB(300)
            .maxBackupIndex(3)
            .buildSingleton();
    }

    public static void log(String resource, String exceptionName, String ruleLimitApp, String origin, int count) {
        statLogger.stat(resource, exceptionName, ruleLimitApp, origin).count(count);
    }
}
