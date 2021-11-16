package cn.torna.web.controller.system.vo;

import lombok.Data;

/**
 * @author wugang
 */
@Data
public class LoginResult {
    private String token;
    private Byte status;
}
