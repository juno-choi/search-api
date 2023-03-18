package com.juno.search.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("execution(* com.juno.search.service..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String requestURI = ((ServletRequestAttributes) requestAttributes).getRequest().getRequestURI();
        String method = ((ServletRequestAttributes) requestAttributes).getRequest().getMethod();
        String id = UUID.randomUUID().toString().replace("-", "").substring(10);

        log.info("[id = {}] [requestUri = ({}) {}, method = {}]", id, method, requestURI, pjp.getSignature().toShortString());

        return pjp.proceed();
    }
}
