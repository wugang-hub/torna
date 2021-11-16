package cn.torna.manager.doc.postman;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import cn.torna.manager.doc.DocParser;
import cn.torna.manager.doc.ParseConfig;

/**
 * @author wugang
 */
@Service("postmanDocParser")
public class PostmanDocParser implements DocParser<Postman> {

    @Override
    public Postman parseJson(String json, ParseConfig config) {
        return JSON.parseObject(json, Postman.class);
    }
}
