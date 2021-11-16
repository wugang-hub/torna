package cn.torna.swaggerplugin.bean;

import lombok.Data;

/**
 * @author wugang
 */
@Data
public class ApiParamInfo {

    String name;

    String value;

    String defaultValue;

    String allowableValues;

    boolean required;

    String access;

    boolean allowMultiple;

    boolean hidden;

    String example;

    int position;
    
}
