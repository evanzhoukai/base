package com.github.liaomengge.base_common.utils.aop;

import java.lang.annotation.Annotation;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/12/20.
 */
@UtilityClass
public class LyAnnotationPointcutUtil {

    /**
     * 类和方法上匹配的注解
     *
     * @param annotationType
     * @return
     */
    public Pointcut buildAnnotationClassOrMethodPointcut(Class<? extends Annotation> annotationType) {
        //class pointcut
        Pointcut classPointcut = new AnnotationMatchingPointcut(annotationType, true);
        //method pointcut
        Pointcut methodPointcut = AnnotationMatchingPointcut.forMethodAnnotation(annotationType);

        //union
        ComposablePointcut pointcut = new ComposablePointcut(classPointcut);
        return pointcut.union(methodPointcut);
    }

    /**
     * 类上匹配的注解
     *
     * @param annotationType
     * @return
     */
    public Pointcut buildAnnotationClassPointcut(Class<? extends Annotation> annotationType) {
        //class pointcut
        return new AnnotationMatchingPointcut(annotationType, true);
    }

    /**
     * 方法上匹配的注解
     *
     * @param annotationType
     * @return
     */
    public Pointcut buildAnnotationMethodPointcut(Class<? extends Annotation> annotationType) {
        //method pointcut
        return AnnotationMatchingPointcut.forMethodAnnotation(annotationType);
    }
}
