package cn.ly.base_common.multi.mybatis.druid;

import cn.ly.base_common.multi.mybatis.druid.properties.DruidStatProperties;
import cn.ly.base_common.multi.mybatis.druid.stat.DruidFilterConfiguration;
import cn.ly.base_common.multi.mybatis.druid.stat.DruidStatViewServletConfiguration;
import cn.ly.base_common.multi.mybatis.druid.stat.DruidWebStatFilterConfiguration;
import cn.ly.base_common.multi.mybatis.druid.wrapper.DruidDataSourceBuilder;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Objects;

/**
 * Created by liaomengge on 2018/12/19.
 */
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidStatProperties.class)
@ConditionalOnProperty(name = "mwee.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource",
        matchIfMissing = true)
@Configuration
@Import({DruidStatViewServletConfiguration.class,
        DruidWebStatFilterConfiguration.class,
        DruidFilterConfiguration.class})
public class DruidConfiguration5 {

    @Bean(name = "fiveParentDataSource")
    @ConfigurationProperties("mwee.mybatis.five.druid")
    public DruidDataSource parentDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(value = "fiveMasterDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("mwee.mybatis.five.druid.master")
    public DruidDataSource masterDataSource(@Qualifier("fiveParentDataSource") DruidDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DruidDataSourceBuilder.create().build();
        }
        return dataSource.cloneDruidDataSource();
    }

    @Bean(value = "fiveSlaveDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("mwee.mybatis.five.druid.slave")
    public DruidDataSource slaveDataSource(@Qualifier("fiveParentDataSource") DruidDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DruidDataSourceBuilder.create().build();
        }
        return dataSource.cloneDruidDataSource();
    }
}