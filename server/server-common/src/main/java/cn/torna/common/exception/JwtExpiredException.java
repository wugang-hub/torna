package cn.torna.common.exception;

/**
 * @author wugang
 */
public class JwtExpiredException extends Exception {
    @Override
    public String getMessage() {
        return "jwt expired";
    }
}
