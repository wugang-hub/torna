package cn.torna.common.enums;

/**
 * @author wugang
 */
public enum StatusEnum {
    DISABLED((byte)0),
    ENABLE((byte)1),
    SET_PWD((byte)2),
    ;

    private final byte status;

    StatusEnum(byte style) {
        this.status = style;
    }

    public byte getStatus() {
        return status;
    }
}
