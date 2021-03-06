package cn.torna.service;

import cn.torna.common.enums.PropTypeEnum;
import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.Prop;
import cn.torna.dao.mapper.PropMapper;
import com.gitee.fastmybatis.core.query.Query;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wugang
 */
@Service
public class PropService extends BaseService<Prop, PropMapper> {

    public void saveProps(Map<String, ?> props, Long refId, PropTypeEnum type) {
        saveProps(props, refId, type.getType());
    }

    public void saveProps(Map<String, ?> props, Long refId, byte type) {
        if (props == null || props.isEmpty()) {
            return;
        }
        List<Prop> tobeSave = props.entrySet()
                .stream()
                .map(entry -> {
                    Prop prop = new Prop();
                    prop.setRefId(refId);
                    prop.setType(type);
                    prop.setName(entry.getKey());
                    prop.setVal(String.valueOf(entry.getValue()));
                    return prop;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(tobeSave)) {
            this.getMapper().saveProps(tobeSave);
        }
    }

    public Map<String, String> getDocProps(Long docId) {
        if (docId == null) {
            return Collections.emptyMap();
        }
        return getProps(docId, PropTypeEnum.DOC_INFO_PROP.getType());
    }

    public Map<String, String> getProps(Long refId, byte type) {
        if (refId == null) {
            return Collections.emptyMap();
        }
        Query query = new Query()
                .eq("ref_id", refId)
                .eq("type", type);
        return this.list(query)
                .stream()
                .collect(Collectors.toMap(Prop::getName, Prop::getVal));
    }

}