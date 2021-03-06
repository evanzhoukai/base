package com.github.liaomengge.service.base_framework.common.filter;

import com.github.liaomengge.service.base_framework.common.filter.chain.ServiceApiFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by liaomengge on 2018/11/21.
 */
public abstract class AbstractFilter implements ServiceApiFilter {

    protected String getMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }
}
