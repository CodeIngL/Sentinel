package com.alibaba.csp.sentinel.util;

import java.lang.management.ManagementFactory;

/**
 * Util class providing pid of current process.
 */
public final class PidUtil {

    public static int getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(name.split("@")[0]);
    }

    private PidUtil() {}
}
