package cn.torna.common.message;

import lombok.Builder;
import lombok.Data;

/**
 * @author wugang
 */
@Data
@Builder
public class Message {
    private String content;
}
