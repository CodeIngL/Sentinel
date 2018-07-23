package com.alibaba.boot.sentinel.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import com.alibaba.boot.sentinel.Constants;
import com.alibaba.boot.sentinel.property.SentinelProperties;
import com.alibaba.boot.sentinel.property.SentinelProperties.ServletFilterConfig;
import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

/**
 * Auto configuration for Sentinel web servlet filter.
 *
 * @author Eric Zhao
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = {Constants.SENTINEL_ENABLED, Constants.SENTINEL_SERVLET_ENABLED}, matchIfMissing = true)
@EnableConfigurationProperties(SentinelProperties.class)
public class SentinelWebServletAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(SentinelWebServletAutoConfiguration.class);

    @Autowired
    private SentinelProperties properties;

    @Bean
    @ConditionalOnWebApplication
    public FilterRegistrationBean sentinelFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        if (!properties.isEnabled()) {
            return registrationBean;
        }

        ServletFilterConfig filterConfig = properties.getServletFilter();
        if (null == filterConfig) {
            filterConfig = new ServletFilterConfig();
            properties.setServletFilter(filterConfig);
        }

        if (CollectionUtils.isEmpty(filterConfig.getUrlPatterns())) {
            List<String> defaultPatterns = new ArrayList<String>();
            defaultPatterns.add("/*");
            filterConfig.setUrlPatterns(defaultPatterns);
            logger.info("[Sentinel Starter] Using default patterns for web servlet filter: {}", defaultPatterns);
        }
        registrationBean.addUrlPatterns(filterConfig.getUrlPatterns().toArray(new String[0]));

        Filter filter = new CommonFilter();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(filterConfig.getOrder());

        logger.info("[Sentinel Starter] Web servlet filter registered with urlPatterns: {}",
            filterConfig.getUrlPatterns());
        return registrationBean;
    }
}
