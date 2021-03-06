package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.ComposeDoc;
import cn.torna.dao.mapper.ComposeDocMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wugang
 */
@Service
public class ComposeDocService extends BaseService<ComposeDoc, ComposeDocMapper> {

    public List<ComposeDoc> listByProjectId(Long projectId) {
        if (projectId == null) {
            return Collections.emptyList();
        }
        Query query = new Query()
                .eq("project_id", projectId)
                .orderby("order_index", Sort.ASC);
        return list(query);
    }


    public List<ComposeDoc> listByProjectIdAndParentId(long projectId, Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        List<ComposeDoc> composeDocList = this.listByProjectId(projectId);
        if (CollectionUtils.isEmpty(composeDocList)) {
            return composeDocList;
        }
        Long finalParentId = parentId;
        return composeDocList.stream()
                .filter(composeDoc -> Objects.equals(composeDoc.getParentId(), finalParentId))
                .collect(Collectors.toList());
    }

}