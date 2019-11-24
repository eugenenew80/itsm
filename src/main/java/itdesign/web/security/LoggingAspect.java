package itdesign.web.security;

import com.google.common.base.Strings;
import itdesign.web.dto.ErrorDto;
import itdesign.web.exc.AccessDeniedException;
import itdesign.web.exc.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {
    protected static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final RedissonClient redissonClient;
    private final Map<String, Set<String>> mapMethodsOnRoles;

    @Around("within(itdesign.web.*)")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        //Выводим название метода и аргументы
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.debug("{} started", methodName);
        logger.debug("argument[s] = {}", Arrays.toString(joinPoint.getArgs()));

        //Получаем ключ сессии
        SecurityManager securityManager = new SecurityManager();
        SessionInfo sessionInfo = securityManager.currentSession();
        if (sessionInfo != null)
            logger.debug("session key: {}", sessionInfo.getSessionKey());

        //Ключ сессии не найден - возвращаем ошибку
        if (Strings.isNullOrEmpty(sessionInfo.getSessionKey())) {
            ErrorDto errorDto = new ErrorDto(new NotAuthorizedException("Not Authorized"));
            return new ResponseEntity<>(errorDto,  errorDto.getErrStatus());
        }

        //Ищем текущего пользователя в redis по ключу сессии
        RBucket<UserInfo> bucket = redissonClient.getBucket(sessionInfo.getSessionKey());
        UserInfo userInfo = bucket.get();
        if (userInfo == null) {
            ErrorDto errorDto = new ErrorDto(new NotAuthorizedException("Not Authorized"));
            return new ResponseEntity<>(errorDto,  errorDto.getErrStatus());
        }
        else {
            logger.debug("user name: {}", userInfo.getUserName());
            logger.debug("region: {}", userInfo.getRegion());
            logger.debug("organ: {}", userInfo.getOrgan());
            logger.debug("roles: {}", userInfo.getRoles());
        }

        if (!mapMethodsOnRoles.containsKey(methodName))
            logger.debug("Access for all authenticated users");

        if (mapMethodsOnRoles.containsKey(methodName)) {
            Set<String> requiredRoles = mapMethodsOnRoles.get(methodName);
            if (!requiredRoles.isEmpty()) {
                for (String role : requiredRoles) {
                    Optional<String> findRole = userInfo.getRoles()
                        .stream()
                        .filter(t -> t.equals(role))
                        .findFirst();

                    if (!findRole.isPresent()) {
                        ErrorDto errorDto = new ErrorDto(new AccessDeniedException("Access denied"));
                        return new ResponseEntity<>(errorDto, errorDto.getErrStatus());
                    }

                    logger.debug("Access granted: {}", findRole.get());
                }
            }
        }

        Object result = joinPoint.proceed();
        logger.trace("result: {}", result);
        logger.debug("{} completed", methodName);
        return result;
    }
}
