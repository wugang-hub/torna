package cn.torna.web.controller.system.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author wugang
 */
@Data
public class UpdatePasswordByFirstLoginParam {

    @NotBlank(message = "密码不能为空")
    private String password;

}
