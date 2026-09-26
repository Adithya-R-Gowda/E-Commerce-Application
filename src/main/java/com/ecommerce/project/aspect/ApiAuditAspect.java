package com.ecommerce.project.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiAuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuditAspect.class);

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logRequestResponseAndTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        // Log request
        logger.info(" [API Request] " + methodName);
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            logger.info(" Arg: " + arg);
        }

        try {
            //Execute Method
            Object result = joinPoint.proceed();

            // Calculate execution time
            long duration = System.currentTimeMillis() - start;

            // Log Response
            logger.info(" [API Response] " + methodName + " -> " + result);
            logger.info(" [Execution Time] " + methodName + " took " + duration + " ms");

            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;
            logger.info("❌ [API ERROR] " + methodName + " threw: " + ex.getMessage());
            logger.info("⏱️  [EXECUTION TIME] " + methodName + " failed in " + duration + " ms");
            throw ex;
        }

    }
}
