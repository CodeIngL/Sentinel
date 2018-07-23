package com.alibaba.boot.sentinel.endpoint;

import com.alibaba.boot.sentinel.property.SentinelProperties;

import org.springframework.boot.actuate.autoconfigure.ManagementContextConfiguration;
import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author duanling
 */
@ManagementContextConfiguration
@EnableConfigurationProperties({SentinelProperties.class})
public class SentinelEndpointManagementContextConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint("sentinel")
    public SentinelActuatorEndpoint sentinelEndPoint() {
        return new SentinelActuatorEndpoint();
    }
}
