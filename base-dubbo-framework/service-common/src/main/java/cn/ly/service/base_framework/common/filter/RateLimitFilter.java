package cn.ly.service.base_framework.common.filter;

import cn.ly.base_common.support.meter._MeterRegistrys;
import cn.ly.base_common.utils.json.LyJacksonUtil;
import cn.ly.base_common.utils.log4j2.LyLogData;
import cn.ly.base_common.utils.number.LyMoreNumberUtil;
import cn.ly.service.base_framework.base.DataResult;
import cn.ly.service.base_framework.common.consts.MetricsConst;
import cn.ly.service.base_framework.common.consts.ServiceConst;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.*;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import io.micrometer.core.instrument.Counter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by liaomengge on 2016/9/20.
 */
public class RateLimitFilter extends AbstractFilter {

    private static final ConcurrentMap<String, RateLimiter> resourceLimiterMap = Maps.newConcurrentMap();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String rateLimitConfig = filterConfig.getRateLimitConfig();
        if (StringUtils.isBlank(rateLimitConfig)) {
            return invoker.invoke(invocation);
        }

        String methodName = invocation.getMethodName();
        Map<String, Object> rateConfigMap;
        try {
            rateConfigMap = LyJacksonUtil.fromJson(rateLimitConfig, Map.class);
        } catch (Exception e) {
            log.warn("限流配置[{}]格式错误,不合法", rateLimitConfig);
            return invoker.invoke(invocation);
        }

        if (Objects.isNull(rateConfigMap) || !rateConfigMap.containsKey(methodName)) {
            return invoker.invoke(invocation);
        }
        double qps = LyMoreNumberUtil.toDouble(MapUtils.getString(rateConfigMap, methodName), -1);
        if (qps <= 0) {
            log.warn("方法[{}],限流配置[{}],QPS配置不合法", methodName, rateLimitConfig);
            return invoker.invoke(invocation);
        }
        RateLimiter rateLimiter = resourceLimiterMap.computeIfAbsent(methodName, key -> RateLimiter.create(qps));
        if (rateLimiter.getRate() != qps) {
            log.info("methodName[{}], 老QPS[{}], 新QPS[{}]", methodName, rateLimiter.getRate(), qps);
            rateLimiter.setRate(qps);
        }

        if (rateLimiter.tryAcquire()) {
            return invoker.invoke(invocation);
        }

        LyLogData logData = new LyLogData();
        logData.setInvocation(invocation.toString());

        RpcContext rpcContext = RpcContext.getContext();
        logData.setRemoteIp(rpcContext.getRemoteAddressString());
        logData.setHostIp(rpcContext.getLocalAddressString());

        Map<String, Object> rpcResponseMap = Maps.newHashMap();
        rpcResponseMap.put("code", ServiceConst.ResponseStatus.ErrorCodeEnum.SERVER_BUSY_ERROR.getCode());
        rpcResponseMap.put("msg", ServiceConst.ResponseStatus.ErrorCodeEnum.SERVER_BUSY_ERROR.getMsg());

        DataResult<Map<String, Object>> dataResult = new DataResult<>(rpcResponseMap);
        RpcResult result = new RpcResult(dataResult);

        URL url = invoker.getUrl();
        String protocol = url.getProtocol();
        String prefix = super.getMetricsPrefixName() + "." + methodName;
        _MeterRegistrys.counter(meterRegistry, prefix + MetricsConst.REQ_EXE_BUSY, PROTOCOL_TAG, protocol).ifPresent(Counter::increment);

        logData.setResult(result.getValue());
        logData.setRestUrl(url.getAbsolutePath());
        log.info(logData);

        return result;
    }
}
