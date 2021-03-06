package com.github.liaomengge.base_common.helper.retrofit.client.interceptor;

import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liaomengge on 2019/11/21.
 */
public class HttpHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String traceId = LyTraceLogUtil.get();
        if (StringUtils.isNotBlank(traceId)) {
            Request.Builder requestBuilder = original.newBuilder().addHeader(LyTraceLogUtil.TRACE_ID, traceId);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
        return chain.proceed(original);
    }
}
