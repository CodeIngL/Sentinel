package com.alibaba.csp.sentinel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.alibaba.csp.sentinel.log.LogBase;
import com.alibaba.csp.sentinel.util.AppNameUtil;

/**
 * Helper class for executing a task within a config context via properties file.
 *
 * @author Eric Zhao
 */
public final class ConfigPropertyHelper {

    public static void setAppNameProperty(String appName) {
        System.setProperty(AppNameUtil.APP_NAME, appName);
    }

    public static void clearAppNameProperty() {
        System.clearProperty(AppNameUtil.APP_NAME);
    }

    public static void runWithConfig(Properties prop, String appName, Task task) throws Exception {
        if (prop == null || appName == null || "".equals(appName)) {
            throw new IllegalArgumentException("Prop and appName cannot be empty");
        }
        // Set application name property.
        setAppNameProperty(appName);
        // Save the config.
        String path = LogBase.getLogBaseDir() + appName + ".properties";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream outputStream = new FileOutputStream(file);
        prop.store(outputStream,"");
        outputStream.close();
        // Run the procedure.
        task.run();
        // Clean-up.
        file.delete();
        // Clear application name property.
        clearAppNameProperty();
    }

    public interface Task {
        void run() throws Exception;
    }

    private ConfigPropertyHelper() {}
}
