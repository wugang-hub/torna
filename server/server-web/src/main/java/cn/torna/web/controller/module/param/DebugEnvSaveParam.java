package cn.torna.web.controller.module.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wugang
 */
@Data
public class DebugEnvSaveParam {

    @NotEmpty
    private List<DebugEnvParam> debugEnvs;

}
