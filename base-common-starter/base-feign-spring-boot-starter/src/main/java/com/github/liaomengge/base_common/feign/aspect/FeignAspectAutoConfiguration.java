package com.github.liaomengge.base_common.feign.aspect;

import com.github.liaomengge.base_common.feign.FeignProperties;
import com.github.liaomengge.base_common.utils.aop.LyAnnotationPointcutUtil;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/10/31.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "base.feign.aspect", name = "enabled", havingValue = "true")
public class FeignAspectAutoConfiguration {

    private final FeignProperties feignProperties;

    public FeignAspectAutoConfiguration(FeignProperties feignProperties) {
        this.feignProperties = feignProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public FeignInterceptor feignInterceptor() {
        return new FeignInterceptor(feignProperties);
    }

    @Bean("feignDefaultPointcutAdvisor")
    @ConditionalOnMissingBean
    public DefaultPointcutAdvisor defaultPointcutAdvisor(FeignInterceptor feignInterceptor) {
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setAdvice(feignInterceptor);
        defaultPointcutAdvisor.setPointcut(LyAnnotationPointcutUtil.buildAnnotationClassPointcut(FeignClient.class));
        return defaultPointcutAdvisor;
    }
}
