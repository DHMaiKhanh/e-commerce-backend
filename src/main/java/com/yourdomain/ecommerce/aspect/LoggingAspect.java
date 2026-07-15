package com.yourdomain.ecommerce.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(com.yourdomain.ecommerce.controller..*)")
    public void controllerLayer() {
    }

    @Pointcut("within(com.yourdomain.ecommerce.service..*)")
    public void serviceLayer() {
    }

    @Around("controllerLayer() || serviceLayer()")
    public Object logExecution(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String target = sig.getDeclaringType().getSimpleName() + "." + sig.getName();
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long took = System.currentTimeMillis() - start;
            if (took > 500) {
                log.warn("SLOW {} took {}ms", target, took);
            } else {
                log.debug("{} took {}ms", target, took);
            }
            return result;
        } catch (Throwable ex) {
            log.error("{} failed: {}", target, ex.getMessage());
            throw ex;
        }
    }
}
