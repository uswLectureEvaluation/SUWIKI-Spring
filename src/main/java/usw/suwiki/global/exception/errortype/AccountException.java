package usw.suwiki.global.exception.errortype;

import usw.suwiki.global.exception.ErrorType;

public class AccountException extends BaseException {

    public AccountException(ErrorType errorType) {
        super(errorType);
    }
}