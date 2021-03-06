package com.github.liaomengge.base_common.dayu.custom;

import com.github.liaomengge.base_common.dayu.custom.consts.CustomCircuitBreakerConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by liaomengge on 2019/6/26.
 */
@Data
@Validated
@ConfigurationProperties(prefix = CustomCircuitBreakerConst.CUSTOM_CIRCUIT_BREAKER_PREFIX)
public class CustomCircuitBreakerProperties {

    private boolean enabled;
    private RuleProperties rule = new RuleProperties();

    @Data
    public static class RuleProperties {
        private Integer failureIntervalSeconds = 20;//请求处理失败的时间区间, 单位:秒
        private Integer failureThreshold = 100;//20秒内100次请求失败

        @NotNull
        private Integer resetMilliSeconds;//间隔时间窗, 单位:毫秒
    }
}
