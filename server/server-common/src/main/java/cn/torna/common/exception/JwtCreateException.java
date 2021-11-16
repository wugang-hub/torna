package cn.torna.common.exception;

/**
 * @author wugang
 */
public class JwtCreateException extends RuntimeException implements ExceptionCode {
    @Override
    public ErrorCode getCode() {
        return ErrorCode.JWT_CREATE;
    }
}
