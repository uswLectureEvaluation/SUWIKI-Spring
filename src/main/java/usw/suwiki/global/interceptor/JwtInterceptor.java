package usw.suwiki.global.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import usw.suwiki.domain.apilogger.service.ApiLoggerService;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.annotation.JWTVerify;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final ApiLoggerService apiLoggerService;
    private final JwtAgent jwtAgent;
    private LocalDateTime startTime;
    private String apiLoggerOption = "";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        this.startTime = LocalDateTime.now();
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            ApiLogger apiLoggerAnnotation = AnnotationUtils.findAnnotation(method, ApiLogger.class);
            JWTVerify annotation = AnnotationUtils.findAnnotation(method, JWTVerify.class);

            if (apiLoggerAnnotation != null) {
                apiLoggerOption = apiLoggerAnnotation.option();
            } else if (annotation != null) {
                String token = request.getHeader("Authorization");
                if (annotation.option().equals("ADMIN")) {
                    if (jwtAgent.getUserRole(token).equals("ADMIN")) {
                        return true;
                    }
                    throw new AccountException(USER_RESTRICTED);
                }
                jwtAgent.validateJwt(token);
            }
        }
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) throws Exception {

    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler, Exception ex
    ) throws Exception {
        LocalDateTime endTime = LocalDateTime.now();
        log.info("{} Api Call startTime = {}, endTime = {}", request.getRequestURI(), startTime, endTime);
        Duration duration = Duration.between(this.startTime, endTime);
        Long finalProcessingTime = duration.toMillis();
        apiLoggerService.logApi(LocalDate.now(), finalProcessingTime, apiLoggerOption);
    }
}
