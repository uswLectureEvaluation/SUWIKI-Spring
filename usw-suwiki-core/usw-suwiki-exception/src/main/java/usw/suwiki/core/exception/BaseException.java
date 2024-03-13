package usw.suwiki.core.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final ExceptionType exceptionType;

    public BaseException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
