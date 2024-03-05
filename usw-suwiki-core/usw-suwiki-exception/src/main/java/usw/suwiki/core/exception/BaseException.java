package usw.suwiki.core.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {

    @Getter
    private final ExceptionType exceptionType;

    public BaseException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
