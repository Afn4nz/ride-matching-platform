package com.ridematch.driver.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;

@Aspect
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    @Before("execution(* com.ridematch.driver.*.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info(
                "({}) | ({}) | Request received with params: ({}). ",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @AfterReturning(
            value = "execution(* com.ridematch.driver.*.controller.*.*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.debug(
                "({}) | ({}) | Returned: ({}).",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(
            value = "execution(* com.ridematch.driver.*.controller.*.*(..))",
            throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error(
                "({}) | ({}) | threw exception [{}] with message: [{}]",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    @After("execution(* com.ridematch.driver.*.controller.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info(
                "({}) | ({}) | Request Completed",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());
    }
}
