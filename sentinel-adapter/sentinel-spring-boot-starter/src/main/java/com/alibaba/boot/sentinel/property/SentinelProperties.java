package com.alibaba.boot.sentinel.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

/**
 * @author Eric Zhao
 */
@ConfigurationProperties(prefix = "spring.sentinel")
public class SentinelProperties {

    private boolean enabled = true;

    private ServletFilterConfig servletFilter;

    public boolean isEnabled() {
        return enabled;
    }

    public SentinelProperties setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ServletFilterConfig getServletFilter() {
        return servletFilter;
    }

    public SentinelProperties setServletFilter(
        ServletFilterConfig servletFilter) {
        this.servletFilter = servletFilter;
        return this;
    }

    public static class DubboFilterConfig {}

    public static class ServletFilterConfig {

        private boolean enabled = true;

        /**
         * Chain order for Sentinel servlet filter.
         */
        private int order = Ordered.HIGHEST_PRECEDENCE;

        /**
         * URL pattern for Sentinel servlet filter.
         */
        private List<String> urlPatterns;

        public int getOrder() {
            return this.order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public List<String> getUrlPatterns() {
            return urlPatterns;
        }

        public void setUrlPatterns(List<String> urlPatterns) {
            this.urlPatterns = urlPatterns;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public ServletFilterConfig setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }
    }
}
