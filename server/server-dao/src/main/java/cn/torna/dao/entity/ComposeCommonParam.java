package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：compose_common_param
 * 备注：聚合文档公共参数
 *
 * @author wugang
 */
@Table(name = "compose_common_param")
@Data
public class ComposeCommonParam {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 唯一id，md5(doc_id:parent_id:style:name), 数据库字段：data_id */
    private String dataId;

    /** 字段名称, 数据库字段：name */
    private String name;

    /** 字段类型, 数据库字段：type */
    private String type;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    private Byte required;

    /** 最大长度, 数据库字段：max_length */
    private String maxLength;

    /** 示例值, 数据库字段：example */
    private String example;

    /** 描述, 数据库字段：description */
    private String description;

    /** enum_info.id, 数据库字段：enum_id */
    private Long enumId;

    /** compose_project.id, 数据库字段：compose_project_id */
    private Long composeProjectId;

    /**  数据库字段：parent_id */
    private Long parentId;

    /** 0：path, 1：header， 2：请求参数，3：返回参数，4：错误码, 数据库字段：style */
    private Byte style;

    /** 排序索引, 数据库字段：order_index */
    private Integer orderIndex;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}