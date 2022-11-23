package usw.suwiki.exception.errortype;

import lombok.Getter;
import usw.suwiki.exception.ErrorType;

public class BaseException extends RuntimeException {

    @Getter
    private ErrorType errorType;

    public BaseException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}