package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author wugang
 */
@Data
public class ComposeProjectAddDTO {
    /** 项目名称, 数据库字段：name */
    private String name;

    /** 项目描述, 数据库字段：description */
    private String description;

    /** 所属组，space.id, 数据库字段：space_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    /** 访问形式，1：公开，2：加密, 数据库字段：type */
    private Byte type;

    /** 1：有效，0：无效, 数据库字段：status */
    private Byte status;

    private Long creatorId;

    private String creatorName;
}
