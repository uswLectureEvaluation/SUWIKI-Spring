package usw.suwiki.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import usw.suwiki.global.exception.errortype.BaseException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static usw.suwiki.global.exception.ErrorType.*;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

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

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        String className = e.getClass().getName();
        ErrorType errorType = e.getErrorType();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .code(errorType.getCode())
                .message(errorType.getMessage())
                .status(errorType.getStatus().value())
                .error(errorType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorType.getStatus());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleBindValidationException(Exception e) {
        String className = e.getClass().getName();
        ErrorType errorType = PARAM_VALID_ERROR;
        String message = "";

        if (e instanceof MethodArgumentNotValidException) {
            message = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } else if (e instanceof BindException) {
            message = ((BindException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .code(errorType.getCode())
                .message(message)
                .status(errorType.getStatus().value())
                .error(errorType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorType.getStatus());
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String className = e.getClass().getName();
        ErrorType errorType = METHOD_NOT_ALLOWED;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .code(errorType.getCode())
                .message(e.getMessage())
                .status(errorType.getStatus().value())
                .error(errorType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorType.getStatus());
    }


    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String className = e.getClass().getName();
        ErrorType errorType = BAD_REQUEST;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .code(errorType.getCode())
                .message(errorType.getMessage())
                .status(errorType.getStatus().value())
                .error(errorType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorType.getStatus());
    }

    @ExceptionHandler(value = {HttpServerErrorException.InternalServerError.class})
    public ResponseEntity<ErrorResponse> InternalServerError(HttpServerErrorException.InternalServerError e) {
        String className = e.getClass().getName();
        ErrorType errorType = SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .code(errorType.getCode())
                .message(errorType.getMessage())
                .status(errorType.getStatus().value())
                .error(errorType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorType.getStatus());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> IllegalArgumentException(IllegalArgumentException e) {
        String className = e.getClass().getName();
        ErrorType errorType = BAD_REQUEST;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .code(errorType.getCode())
                .message(errorType.getMessage())
                .status(errorType.getStatus().value())
                .error(errorType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorType.getStatus());
    }
}
