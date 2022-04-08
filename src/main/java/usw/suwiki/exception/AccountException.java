package usw.suwiki.exception;

public class AccountException extends BaseException {

    public AccountException(ErrorType errorType) {
        super(errorType);
    }
}