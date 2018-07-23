package com.alibaba.boot.sentinel;

/**
 * @author Eric Zhao
 */
public final class Constants {

    /**
     * Property prefix in application.properties.
     */
    public static final String PREFIX = "spring.sentinel";
    public static final String SENTINEL_SERVLET_ENABLED = "spring.sentinel.servletFilter.enabled";
    public static final String SENTINEL_ENABLED = "spring.sentinel.enabled";

    private Constants() {}
}
