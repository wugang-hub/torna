package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：user_subscribe
 * 备注：用户订阅表
 *
 * @author wugang
 */
@Table(name = "user_subscribe")
@Data
public class UserSubscribe {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** user_info.id, 数据库字段：user_id */
    private Long userId;

    /** 类型，1：订阅接口，2：订阅项目, 数据库字段：type */
    private Byte type;

    /** 关联id，当type=1对应doc_info.id；type=2对应project.id, 数据库字段：source_id */
    private Long sourceId;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}