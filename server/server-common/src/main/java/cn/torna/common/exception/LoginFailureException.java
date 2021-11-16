package cn.torna.common.exception;

/**
 * @author wugang
 */
public class LoginFailureException extends RuntimeException implements ExceptionCode {
    @Override
    public ErrorCode getCode() {
        return ErrorCode.LOGIN_FAIL;
    }

    public LoginFailureException(String message) {
        super(message);
    }

    public LoginFailureException() {
    }
}
