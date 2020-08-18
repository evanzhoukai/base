package cn.ly.base_common.dayu.sentinel.datasource;

import cn.ly.base_common.dayu.sentinel.consts.SentinelConst;
import cn.ly.base_common.utils.log4j2.LyLogger;
import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.Converter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Created by liaomengge on 2019/8/9.
 */
public class GiraffeDataSource<T> extends AbstractDataSource<String, T> implements EnvironmentAware,
        ApplicationListener<EnvironmentChangeEvent> {

    private static final Logger log = LyLogger.getInstance(GiraffeDataSource.class);

    private Environment environment;

    private String rule;

    public GiraffeDataSource(Converter<String, T> parser, String rule) {
        super(parser);
        this.rule = rule;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String readSource() throws Exception {
        return this.environment.getProperty(this.buildRuleKey());
    }

    @Override
    public void close() throws Exception {
    }

    @PostConstruct
    private void init() {
        loadAndUpdateRules();
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Optional<String> sentinelChange = event.getKeys().stream().filter(val -> StringUtils.startsWithIgnoreCase(val,
                buildRuleKey())).findFirst();
        sentinelChange.ifPresent(val -> loadAndUpdateRules());
    }

    private void loadAndUpdateRules() {
        try {
            T newValue = loadConfig();
            if (newValue == null) {
                log.info("[GiraffeDataSource] INFO: [{}] rule config is null, you may have to check your data " +
                        "source", this.rule);
            }
            if (getProperty().updateValue(newValue)) {
                log.info("[DynamicSentinelProperty] [" + this.rule + "] rule config be updated to: " + newValue);
            }
        } catch (Throwable ex) {
            log.warn("[GiraffeDataSource] Error when loading [" + this.rule + "] rule config", ex);
        }
    }

    private String buildRuleKey() {
        return SentinelConst.SENTINEL_PREFIX + ".rule." + rule;
    }
}
