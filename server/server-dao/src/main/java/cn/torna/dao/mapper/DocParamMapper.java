package cn.torna.dao.mapper;

import cn.torna.dao.entity.DocParam;
import com.gitee.fastmybatis.core.mapper.CrudMapper;

/**
 * @author wugang
 */
public interface DocParamMapper extends CrudMapper<DocParam, Long> {

    int saveParam(DocParam docParam);

}