package cn.ly.base_common.metric.datasource.hikari;

import cn.ly.base_common.utils.log4j2.LyLogger;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.slf4j.Logger;
import org.springframework.boot.jdbc.DataSourceUnwrapper;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static cn.ly.base_common.metric.consts.MetricsConst.HIKARI_PREFIX;

/**
 * Created by liaomengge on 2020/9/17.
 */
public class HikariMetricsBinder implements MeterBinder {

    private static final Logger log = LyLogger.getInstance(HikariMetricsBinder.class);

    private final Iterable<Tag> tags;
    private final List<DataSource> dataSources;

    public HikariMetricsBinder(List<DataSource> dataSources) {
        this(Collections.emptyList(), dataSources);
    }

    public HikariMetricsBinder(Iterable<Tag> tags, List<DataSource> dataSources) {
        this.tags = tags;
        this.dataSources = dataSources;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            if (Objects.nonNull(dataSources)) {
                List<HikariDataSource> hikariDataSources =
                        dataSources.stream().map(dataSource -> DataSourceUnwrapper.unwrap(dataSource,
                                HikariDataSource.class))
                                .filter(Objects::nonNull).collect(Collectors.toList());
                registerMetrics(registry, hikariDataSources);
            }
        } catch (Exception e) {
            log.error("metric hikari error", e);
        }
    }

    private void registerMetrics(MeterRegistry registry, List<HikariDataSource> hikariDataSources) {
        hikariDataSources.forEach(hikariDataSource -> {
            HikariPoolMXBean hikariPoolMXBean = hikariDataSource.getHikariPoolMXBean();
            if (Objects.nonNull(hikariPoolMXBean)) {
                bindGauge(registry, hikariDataSource.getPoolName(), HIKARI_PREFIX + "active.connections",
                        hikariPoolMXBean, HikariPoolMXBean::getActiveConnections);
                bindGauge(registry, hikariDataSource.getPoolName(), HIKARI_PREFIX + "idle.connections",
                        hikariPoolMXBean, HikariPoolMXBean::getIdleConnections);
                bindGauge(registry, hikariDataSource.getPoolName(), HIKARI_PREFIX + "total.connections",
                        hikariPoolMXBean, HikariPoolMXBean::getTotalConnections);
                bindGauge(registry, hikariDataSource.getPoolName(), HIKARI_PREFIX + "threads.awaiting.connection",
                        hikariPoolMXBean, HikariPoolMXBean::getThreadsAwaitingConnection);
            }
            HikariConfigMXBean hikariConfigMXBean = hikariDataSource.getHikariConfigMXBean();
            if (Objects.nonNull(hikariConfigMXBean)) {
                bindGauge(registry, hikariDataSource.getPoolName(), HIKARI_PREFIX + "maximum.pool.size",
                        hikariConfigMXBean, HikariConfigMXBean::getMaximumPoolSize);
                bindGauge(registry, hikariDataSource.getPoolName(), HIKARI_PREFIX + "minimum.idle",
                        hikariConfigMXBean, HikariConfigMXBean::getMinimumIdle);
            }
        });
    }

    private void bindGauge(MeterRegistry registry, String poolName, String name, HikariPoolMXBean hikariPoolMXBean,
                           ToDoubleFunction<HikariPoolMXBean> function) {
        Gauge.builder(name, hikariPoolMXBean, function)
                .tags(Tags.concat(tags, "pool.name", poolName)).register(registry);
    }

    private void bindGauge(MeterRegistry registry, String poolName, String name, HikariConfigMXBean hikariConfigMXBean,
                           ToDoubleFunction<HikariConfigMXBean> function) {
        Gauge.builder(name, hikariConfigMXBean, function)
                .tags(Tags.concat(tags, "pool.name", poolName)).register(registry);
    }
}