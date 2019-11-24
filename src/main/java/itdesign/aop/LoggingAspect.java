package itdesign.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class LoggingAspect {
    protected static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(itdesign.web.*)")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.debug("{}.{} started", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        logger.debug("Argument[s] = {}", Arrays.toString(joinPoint.getArgs()));

        Object result = joinPoint.proceed();
        logger.trace("Result: {}", result);
        logger.debug("{}.{} completed", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        return result;
    }
}
