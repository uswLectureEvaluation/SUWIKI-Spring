package usw.suwiki.global.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static usw.suwiki.global.exception.ExceptionType.PARAM_VALID_ERROR;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import usw.suwiki.global.exception.errortype.BaseException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        HttpStatus status = INTERNAL_SERVER_ERROR;
        String code = "NO_CATCH_ERROR";
        String className = e.getClass().getName();
        String message = e.getMessage();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .code(code)
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .build();

        log.error("code : {}, message : {}", errorResponse.getCode(), errorResponse.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        String className = e.getClass().getName();
        ExceptionType exceptionType = e.getExceptionType();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .code(exceptionType.getCode())
                .message(exceptionType.getMessage())
                .status(exceptionType.getStatus().value())
                .error(exceptionType.getStatus().getReasonPhrase())
                .build();

        log.error("code : {}, message : {}", errorResponse.getCode(), errorResponse.getMessage());

        return new ResponseEntity<>(errorResponse, exceptionType.getStatus());
    }

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            TypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleRequestValidationException(Exception e) {

        ExceptionType exception = PARAM_VALID_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(exception.name())
                .code(exception.getCode())
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(exception.getStatus().getReasonPhrase())
                .build();

        log.error("code : {}, message : {}", errorResponse.getCode(), errorResponse.getMessage());

        return new ResponseEntity<>(errorResponse, exception.getStatus());
    }
}
