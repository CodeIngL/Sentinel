package com.alibaba.csp.sentinel.config;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.csp.sentinel.log.LogBase;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * The universal config of Courier. The config is retrieved from
 * {@code ${user.home}/logs/csp/${appName}.properties} by default.
 * <p>
 * <p>
 * Sentinel的通用性配置。
 * </p>
 * <p>
 * 默认情况下，从{@code ${user.home}/logs/csp/${appName}.properties} 中检索配置。
 * </p>
 * <p>
 * 在触发类加载时，由{@code static}语句引入初始化。
 * </p>
 *
 * @author leyou
 */
public class SentinelConfig {

    private static final Map<String, String> props = new ConcurrentHashMap<String, String>();

    public static final String CHARSET = "csp.sentinel.charset";
    public static final String SINGLE_METRIC_FILE_SIZE = "csp.sentinel.metric.file.single.size";
    public static final String TOTAL_METRIC_FILE_COUNT = "csp.sentinel.metric.file.total.count";
    public static final String COLD_FACTOR = "csp.sentinel.flow.cold.factor";

    static final long DEFAULT_SINGLE_METRIC_FILE_SIZE = 1024 * 1024 * 50;
    static final int DEFAULT_TOTAL_METRIC_FILE_COUNT = 6;

    static {
        initialize();
        loadProps();
    }

    /**
     * 初始化默认的配置
     * 编码:
     * 文件大小:
     * 文件最多数量
     * 冷启动方式
     */
    private static void initialize() {
        // Init default properties.
        SentinelConfig.setConfig(CHARSET, "UTF-8");
        SentinelConfig.setConfig(SINGLE_METRIC_FILE_SIZE, String.valueOf(DEFAULT_SINGLE_METRIC_FILE_SIZE));
        SentinelConfig.setConfig(TOTAL_METRIC_FILE_COUNT, String.valueOf(DEFAULT_TOTAL_METRIC_FILE_COUNT));
        SentinelConfig.setConfig(COLD_FACTOR, String.valueOf(3));
    }

    private static void loadProps() {
        // Resolve app name.
        AppNameUtil.resolveAppName();
        try {
            String appName = AppNameUtil.getAppName();
            if (appName == null) {
                appName = "";
            }
            // We first retrieve the properties from the property file.
            String fileName = LogBase.getLogBaseDir() + appName + ".properties";

            FileInputStream fis = new FileInputStream(fileName);
            Properties fileProps = new Properties();
            fileProps.load(fis);
            fis.close();

            for (Object key : fileProps.keySet()) {
                SentinelConfig.setConfig((String) key, (String) fileProps.get(key));
                try {
                    String systemValue = System.getProperty((String) key);
                    if (!StringUtil.isEmpty(systemValue)) {
                        SentinelConfig.setConfig((String) key, systemValue);
                    }
                } catch (Exception e) {
                    RecordLog.info(e.getMessage(), e);
                }
                RecordLog.info(key + " value: " + SentinelConfig.getConfig((String) key));
            }

        } catch (Throwable ioe) {
            RecordLog.info(ioe.getMessage(), ioe);
        }

        // JVM parameter override file config.
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            SentinelConfig.setConfig(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    /**
     * Get config value of the specific key.
     *
     * @param key config key
     * @return the config value.
     */
    public static String getConfig(String key) {
        return props.get(key);
    }

    public static void setConfig(String key, String value) {
        props.put(key, value);
    }

    public static void setConfigIfAbsent(String key, String value) {
        String v = props.get(key);
        if (v == null) {
            props.put(key, value);
        }
    }

    public static String getAppName() {
        return AppNameUtil.getAppName();
    }

    public static String charset() {
        return props.get(CHARSET);
    }

    public static long singleMetricFileSize() {
        try {
            return Long.parseLong(props.get(SINGLE_METRIC_FILE_SIZE));
        } catch (Throwable throwable) {
            RecordLog.info("SentinelConfig get singleMetricFileSize fail, use default value: "
                    + DEFAULT_SINGLE_METRIC_FILE_SIZE, throwable);
            return DEFAULT_SINGLE_METRIC_FILE_SIZE;
        }
    }

    public static int totalMetricFileCount() {
        try {
            return Integer.parseInt(props.get(TOTAL_METRIC_FILE_COUNT));
        } catch (Throwable throwable) {
            RecordLog.info("SentinelConfig get totalMetricFileCount fail, use default value: "
                    + DEFAULT_TOTAL_METRIC_FILE_COUNT, throwable);
            return DEFAULT_TOTAL_METRIC_FILE_COUNT;
        }
    }
}
