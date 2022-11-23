package usw.suwiki.exception.errortype;

import usw.suwiki.exception.ErrorType;

public class AccountException extends BaseException {

    public AccountException(ErrorType errorType) {
        super(errorType);
    }
}