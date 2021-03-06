package com.github.liaomengge.base_common.mq.activemq.monitor;

import com.github.liaomengge.base_common.mq.consts.MQConst;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Setter;

import java.time.Duration;

/**
 * Created by liaomengge on 17/2/13.
 */
public class DefaultMQMonitor {

    @Setter
    private MeterRegistry meterRegistry;

    public void monitorCount(String metricsActiveMQ) {
        final String metricsPrefix = MQConst.ActiveMQ.MONITOR_PREFIX;
        _MeterRegistrys.counter(meterRegistry, metricsPrefix + metricsActiveMQ).ifPresent(Counter::increment);
    }

    public void monitorTime(String metricsActiveMQ, long elapsedNanoTime) {
        final String metricsPrefix = MQConst.ActiveMQ.MONITOR_PREFIX;
        _MeterRegistrys.timer(meterRegistry, metricsPrefix + metricsActiveMQ).ifPresent(val -> val.record(Duration.ofNanos(elapsedNanoTime)));
    }

}
