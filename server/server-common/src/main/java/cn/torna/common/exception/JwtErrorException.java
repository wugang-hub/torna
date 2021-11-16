package cn.torna.common.exception;

/**
 * @author wugang
 */
public class JwtErrorException extends Exception {

    @Override
    public String getMessage() {
        return "jwt verify error";
    }
}
