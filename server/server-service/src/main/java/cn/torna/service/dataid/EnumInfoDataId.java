package cn.torna.service.dataid;

import cn.torna.common.util.DataIdUtil;

/**
 * @author wugang
 */
public interface EnumInfoDataId {

    /**
     * 唯一id，md5(module_id:name)
     * @return
     */
    default String buildDataId() {
        return DataIdUtil.getEnumInfoDataId(getModuleId(), getName());
    }

    Long getModuleId();

    String getName();

}
