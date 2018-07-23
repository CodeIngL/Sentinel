package com.alibaba.csp.sentinel.eagleeye;

class BaseLoggerBuilder<T extends BaseLoggerBuilder<T>> {

    protected final String loggerName;

    protected String filePath = null;

    protected long maxFileSize = 1024;

    protected char entryDelimiter = '|';

    protected int maxBackupIndex = 3;

    BaseLoggerBuilder(String loggerName) {
        this.loggerName = loggerName;
    }

    public T logFilePath(String logFilePath) {
        return configLogFilePath(logFilePath, EagleEye.EAGLEEYE_LOG_DIR);
    }

    public T appFilePath(String appFilePath) {
        return configLogFilePath(appFilePath, EagleEye.APP_LOG_DIR);
    }

    public T baseLogFilePath(String baseLogFilePath) {
        return configLogFilePath(baseLogFilePath, EagleEye.BASE_LOG_DIR);
    }

    @SuppressWarnings("unchecked")
    private T configLogFilePath(String filePathToConfig, String basePath) {
        EagleEyeCoreUtils.checkNotNullEmpty(filePathToConfig, "filePath");
        if (filePathToConfig.charAt(0) != '/') {
            filePathToConfig = basePath + filePathToConfig;
        }
        this.filePath = filePathToConfig;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T maxFileSizeMB(long maxFileSizeMB) {
        if (maxFileSize < 10) {
            throw new IllegalArgumentException("Invalid maxFileSizeMB");
        }
        this.maxFileSize = maxFileSizeMB * 1024 * 1024;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T maxBackupIndex(int maxBackupIndex) {
        if (maxBackupIndex < 1) {
            throw new IllegalArgumentException("");
        }
        this.maxBackupIndex = maxBackupIndex;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T entryDelimiter(char entryDelimiter) {
        this.entryDelimiter = entryDelimiter;
        return (T)this;
    }

    String getLoggerName() {
        return loggerName;
    }
}
