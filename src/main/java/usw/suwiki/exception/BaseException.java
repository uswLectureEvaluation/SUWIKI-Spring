package usw.suwiki.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {

    @Getter
    private ErrorType errorType;

    public BaseException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}