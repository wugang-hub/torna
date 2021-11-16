package cn.torna.common.enums;

/**
 * @author wugang
 */
public enum ComposeProjectTypeEnum {
    PUBLIC((byte)1),
    ENCRYPT((byte) 2),
    ;
    private final byte type;

    ComposeProjectTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
