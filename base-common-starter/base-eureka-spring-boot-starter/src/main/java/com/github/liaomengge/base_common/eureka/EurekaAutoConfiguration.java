package com.github.liaomengge.base_common.eureka;

import com.github.liaomengge.base_common.eureka.endpoint.EurekaPullInEndpoint;
import com.github.liaomengge.base_common.eureka.endpoint.EurekaPullOutEndpoint;
import com.github.liaomengge.base_common.eureka.endpoint.process.EurekaEndpointBeanPostProcess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/8/15.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({EurekaRegistration.class, EurekaServiceRegistry.class})
@ConditionalOnDiscoveryEnabled
@EnableConfigurationProperties(EurekaProperties.class)
public class EurekaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EurekaPullInEndpoint eurekaPullInEndpoint() {
        return new EurekaPullInEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public EurekaPullOutEndpoint eurekaPullOutEndpoint() {
        return new EurekaPullOutEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public static EurekaEndpointBeanPostProcess eurekaBeanPostProcess() {
        return new EurekaEndpointBeanPostProcess();
    }
}
