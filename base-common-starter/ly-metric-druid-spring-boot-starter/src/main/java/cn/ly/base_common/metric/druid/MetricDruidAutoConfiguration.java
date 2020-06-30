package cn.ly.base_common.metric.druid;

import cn.ly.base_common.metric.druid.task.MetricDruidScheduledTask;
import com.alibaba.druid.pool.DruidDataSource;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/7/24.
 */
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnProperty(prefix = "mwee.metric-druid", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MetricDruidProperties.class)
public class MetricDruidAutoConfiguration {

    @Autowired
    private MetricDruidProperties metricDruidProperties;

    @Bean
    @ConditionalOnMissingBean
    public MetricDruidScheduledTask metricDruidScheduledTask(StatsDClient statsDClient) {
        return new MetricDruidScheduledTask(statsDClient, metricDruidProperties);
    }
}