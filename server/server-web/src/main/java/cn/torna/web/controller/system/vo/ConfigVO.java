package cn.torna.web.controller.system.vo;

import lombok.Data;

/**
 * @author wugang
 */
@Data
public class ConfigVO {

    private boolean enableReg;
    private boolean enableThirdPartyForm;
    private boolean enableThirdPartyOauth;
    private boolean enableThirdPartyLogin;
    private String oauthLoginUrl;
    private String oauthButtonText;
    private boolean ignoreParam;

}
