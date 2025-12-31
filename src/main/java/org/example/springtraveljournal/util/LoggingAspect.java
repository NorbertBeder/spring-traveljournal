package org.example.springtraveljournal.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info(" --- ENTER {} with args {}", methodName, args);

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;
            log.info(" --- EXIT {} ({} ms)", methodName, duration);

            return result;

        } catch (Throwable ex) {

            long duration = System.currentTimeMillis() - start;
            log.warn(" --- ERROR in {} ({} ms): {} ---",
                    methodName,
                    duration,
                    ex.getMessage());

            throw ex;
        }
    }
}
