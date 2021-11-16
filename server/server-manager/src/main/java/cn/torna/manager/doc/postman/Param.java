package cn.torna.manager.doc.postman;

import lombok.Getter;
import lombok.Setter;
import cn.torna.manager.doc.IParam;

import java.util.List;

/**
 * @author wugang
 */
@Getter
@Setter
public class Param implements IParam {
    private String key;
    private String value;
    private String type;
    private String description;

    private List<Param> children;

    @Override
    public String getName() {
        return key;
    }

    @Override
    public boolean getRequired() {
        return true;
    }

    @Override
    public String getMaxLength() {
        return "";
    }

    @Override
    public String getExample() {
        return value;
    }
}
