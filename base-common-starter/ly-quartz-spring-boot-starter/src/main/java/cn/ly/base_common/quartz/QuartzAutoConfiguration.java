package cn.ly.base_common.quartz;


import cn.ly.base_common.helper.concurrent.MwThreadPoolTaskWrappedExecutor;
import cn.ly.base_common.quartz.listener.QuartzListener;
import cn.ly.base_common.quartz.registry.QuartzBeanRegistryConfiguration;
import cn.ly.base_common.utils.log4j2.MwLogger;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static cn.ly.base_common.quartz.registry.QuartzBeanDefinitionRegistry.getApplicationContext;
import static cn.ly.base_common.quartz.registry.QuartzBeanDefinitionRegistry.getJobBeanMap;

/**
 * Created by liaomengge on 2019/1/29.
 */
@Configuration
@EnableConfigurationProperties(QuartzProperties.class)
@ConditionalOnClass({JobDetail.class, CronTrigger.class, SchedulerFactoryBean.class})
@Import(QuartzBeanRegistryConfiguration.class)
public class QuartzAutoConfiguration {

    private static final Logger logger = MwLogger.getInstance(QuartzAutoConfiguration.class);

    private final CopyOnWriteArrayList triggerKeyList = Lists.newCopyOnWriteArrayList();

    @Autowired
    private QuartzProperties quartzProperties;

    @Bean
    public QuartzListener quartzListener() {
        return new QuartzListener(triggerKeyList);
    }

    @Bean
    @Primary
    public SchedulerFactoryBean schedulerFactoryBean() {
        List<CronTrigger> cronTriggerList = this.buildJobTrigger();
        if (cronTriggerList.isEmpty()) {
            logger.warn("not found corn trigger, please check quartz properties!!!");
            throw new BeanInstantiationException(SchedulerFactoryBean.class, "not found corn trigger, " +
                    "please check quartz properties");
        }
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setStartupDelay(this.quartzProperties.getStartupDelay());
        schedulerFactoryBean.setAutoStartup(Boolean.FALSE.booleanValue());
        schedulerFactoryBean.setOverwriteExistingJobs(this.quartzProperties.isOverwriteExistingJobs());
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(this.quartzProperties.isWaitForJobsToCompleteOnShutdown());
        schedulerFactoryBean.setTriggers(cronTriggerList.stream().toArray(CronTrigger[]::new));
        String taskExecutorRef = this.quartzProperties.getTaskExecutorRef();
        if (StringUtils.isNotBlank(taskExecutorRef)) {
            MwThreadPoolTaskWrappedExecutor taskWrappedExecutor =
                    getApplicationContext().getBean(this.quartzProperties.getTaskExecutorRef(),
                            MwThreadPoolTaskWrappedExecutor.class);
            if (Objects.nonNull(taskWrappedExecutor)) {
                schedulerFactoryBean.setTaskExecutor(taskWrappedExecutor);
            }
        }
        return schedulerFactoryBean;
    }

    public List<CronTrigger> buildJobTrigger() {
        List<CronTrigger> cronTriggerList = Lists.newArrayList();
        List<JobDetailImpl> jobDetailList = this.buildJobDetail();
        for (JobDetailImpl jobDetail : jobDetailList) {
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            String triggerName = jobDetail.getName() + "Trigger";
            cronTriggerFactoryBean.setName(triggerName);
            cronTriggerFactoryBean.setJobDetail(jobDetail);
            QuartzProperties.JobInfo jobInfo = this.quartzProperties.findClassName(jobDetail.getName());
            if (Objects.isNull(jobInfo)) {
                continue;
            }
            cronTriggerFactoryBean.setCronExpression(jobInfo.getCronExpression());
            cronTriggerFactoryBean.setMisfireInstruction(jobInfo.getMisfireInstruction());
            try {
                cronTriggerFactoryBean.afterPropertiesSet();
                CronTrigger cronTrigger = cronTriggerFactoryBean.getObject();
                cronTriggerList.add(cronTrigger);
                triggerKeyList.add(cronTrigger.getKey());
            } catch (Exception e) {
                logger.warn("load job trigger class[" + triggerName + "]fail!!!", e);
            }
        }
        logger.info("load job trigger(" + cronTriggerList.size() + ") ===> [" +
                cronTriggerList.parallelStream().map(val -> val.getKey().getName()).reduce((val, val2) -> val + ',' + val2).orElse("") + "]");
        return cronTriggerList;
    }

    public List<JobDetailImpl> buildJobDetail() {
        List<JobDetailImpl> jobDetailList = Lists.newArrayList();
        Map<String, Object> beanMap = getJobBeanMap();
        if (beanMap.isEmpty()) {
            logger.warn("the package[" + this.quartzProperties.getBasePackage() + "]'s class don't exist or not " +
                    "inherit AbstractBaseJob");
            return jobDetailList;
        }
        List<QuartzProperties.JobInfo> jobInfoList = this.quartzProperties.getJobs();
        for (QuartzProperties.JobInfo jobInfo : jobInfoList) {
            String pkgClassName = this.quartzProperties.getPackageName(jobInfo);
            Object obj = beanMap.get(pkgClassName);
            if (Objects.isNull(obj)) {
                logger.warn("not found class[{}], please check quartz properties(" +
                        "1.inherit AbstractBaseJob;2.{} must exist)!!!", pkgClassName, pkgClassName);
                continue;
            }
            MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean =
                    new MethodInvokingJobDetailFactoryBean();
            methodInvokingJobDetailFactoryBean.setName(pkgClassName);
            methodInvokingJobDetailFactoryBean.setTargetObject(obj);
            methodInvokingJobDetailFactoryBean.setTargetMethod("execute");
            methodInvokingJobDetailFactoryBean.setConcurrent(jobInfo.isConcurrent());
            try {
                methodInvokingJobDetailFactoryBean.afterPropertiesSet();
                JobDetailImpl jobDetail = (JobDetailImpl) methodInvokingJobDetailFactoryBean.getObject();
                jobDetailList.add(jobDetail);
            } catch (Exception e) {
                logger.warn("load job detail class[" + pkgClassName + "]fail!!!", e);
            }
        }
        return jobDetailList;
    }
}