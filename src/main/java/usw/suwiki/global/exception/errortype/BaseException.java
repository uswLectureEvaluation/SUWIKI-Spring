package usw.suwiki.global.exception.errortype;

import lombok.Getter;
import usw.suwiki.global.exception.ExceptionType;

public class BaseException extends RuntimeException {

    @Getter
    private ExceptionType exceptionType;

    public BaseException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}