package usw.suwiki.statistics.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import usw.suwiki.statistics.util.CacheStaticsLogger;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheStaticsAspect {

    private final HttpServletRequest httpServletRequest;
    private final CacheStaticsLogger cacheStaticsLogger;

    @Around("@annotation(usw.suwiki.statistics.annotation.CacheStatics)")
    public Object execute(ProceedingJoinPoint pjp) throws Throwable {
        cacheStaticsLogger.getCachesStats(httpServletRequest.getRequestURI().split("/")[1]);

        return pjp.proceed(pjp.getArgs());
    }
}
