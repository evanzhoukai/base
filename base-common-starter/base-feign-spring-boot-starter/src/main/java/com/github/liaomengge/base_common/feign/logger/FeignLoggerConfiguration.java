package com.github.liaomengge.base_common.feign.logger;

import com.github.liaomengge.base_common.feign.FeignProperties;
import feign.Logger;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by liaomengge on 2020/10/29.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "base.feign.logger", name = "enabled", havingValue = "true")
@AutoConfigureBefore(FeignClientsConfiguration.class)
public class FeignLoggerConfiguration {

    private final FeignProperties feignProperties;

    public FeignLoggerConfiguration(FeignProperties feignProperties) {
        this.feignProperties = feignProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    @Primary
    @ConditionalOnClass(feign.Logger.class)
    public Logger logger() {
        return new FeignLogger(feignProperties);
    }
}
