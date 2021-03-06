package cn.torna.dao.mapper;

import com.gitee.fastmybatis.core.mapper.CrudMapper;
import cn.torna.dao.entity.DocInfo;

/**
 * @author wugang
 */
public interface DocInfoMapper extends CrudMapper<DocInfo, Long> {

    int saveDocInfo(DocInfo docInfo);

}