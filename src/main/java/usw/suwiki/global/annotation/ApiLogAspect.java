package usw.suwiki.global.annotation;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import usw.suwiki.domain.apilogger.service.ApiLoggerService;

@Component
@Aspect
@Log4j2
@RequiredArgsConstructor
public class ApiLogAspect {

    private final ApiLoggerService apiLoggerService;

    @Around("@annotation(ApiLogger)")
    public Object logExecutionTime(
        ProceedingJoinPoint joinPoint, ApiLogger ApiLogger
    ) throws Throwable
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object proceed = joinPoint.proceed();

        stopWatch.stop();

        String option = ApiLogger.option();

        apiLoggerService.logApi(LocalDate.now(), stopWatch.getTotalTimeMillis(), option);
        return proceed;
    }
}
