package cn.torna.manager.doc.postman;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author wugang
 */
@Getter
@Setter
public class Postman {
    private Info info;
    private List<Item> item;
}
