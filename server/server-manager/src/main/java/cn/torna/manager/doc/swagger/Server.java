package cn.torna.manager.doc.swagger;

import lombok.Data;

/**
 * @author wugang
 */
@Data
public class Server {
    private String url;
    private String description;

    public Server() {
    }

    public Server(String url, String description) {
        this.url = url;
        this.description = description;
    }
}
