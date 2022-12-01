package usw.suwiki.global.exception.errortype;

import lombok.Getter;
import usw.suwiki.global.exception.ErrorType;

public class BaseException extends RuntimeException {

    @Getter
    private ErrorType errorType;

    public BaseException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}