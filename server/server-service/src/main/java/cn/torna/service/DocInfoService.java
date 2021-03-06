package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.enums.OperationMode;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.enums.PropTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdGen;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.ModuleConfig;
import cn.torna.dao.mapper.DocInfoMapper;
import cn.torna.service.dto.DebugHostDTO;
import cn.torna.service.dto.DocFolderCreateDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocItemCreateDTO;
import cn.torna.service.dto.DocMeta;
import cn.torna.service.dto.DocParamDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.query.param.SchPageableParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wugang
 */
@Service
public class DocInfoService extends BaseService<DocInfo, DocInfoMapper> {

    @Autowired
    private DocParamService docParamService;

    @Autowired
    private ModuleConfigService moduleConfigService;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private PropService propService;

    /**
     * ??????????????????????????????
     * @param moduleId ??????id
     * @return ????????????
     */
    public List<DocInfo> listModuleDoc(long moduleId) {
        List<DocInfo> docInfoList = list("module_id", moduleId);
        docInfoList.sort(Comparator.comparing(DocInfo::getOrderIndex));
        return docInfoList;
    }

    public List<DocInfo> listDocMenuView(long moduleId) {
        return this.listModuleDoc(moduleId)
                .stream()
                .filter(docInfo -> docInfo.getIsShow() == Booleans.TRUE)
                .collect(Collectors.toList());
    }

    public PageEasyui<DocInfo> pageDocByIds(List<Long> docIds, SchPageableParam pageParam) {
        if (CollectionUtils.isEmpty(docIds)) {
            return new PageEasyui<>();
        }
        Query query = new Query()
                .in("id", docIds)
                .limit(pageParam.getStart(), pageParam.getLimit())
                .orderby("order_index", Sort.ASC);
        return MapperUtil.queryForEasyuiDatagrid(this.getMapper(), query);
    }

    /**
     * ??????????????????
     * @param docId ??????id
     * @return ??????????????????
     */
    public DocInfoDTO getDocDetailView(long docId) {
        Query query = new Query()
                .eq("id", docId)
                .eq("is_show", Booleans.TRUE);
        DocInfo docInfo = get(query);
        return getDocDetail(docInfo);
    }

    /**
     * ??????????????????
     * @param docId ??????id
     * @return ??????????????????
     */
    public DocInfoDTO getDocDetail(long docId) {
        DocInfo docInfo = this.getById(docId);
        return getDocDetail(docInfo);
    }

    /**
     * ??????????????????
     * @param docId ??????id
     * @return ??????????????????
     */
    public DocInfoDTO getDocForm(long docId) {
        DocInfo docInfo = this.getById(docId);
        return getDocInfoDTO(docInfo);
    }

    private DocInfoDTO getDocInfoDTO(DocInfo docInfo) {
        Assert.notNull(docInfo, () -> "???????????????");
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docInfo, DocInfoDTO::new);
        Long moduleId = docInfo.getModuleId();
        Module module = moduleService.getById(moduleId);
        docInfoDTO.setProjectId(module.getProjectId());
        docInfoDTO.setModuleType(module.getType());
        List<ModuleConfig> debugEnvs = moduleConfigService.listDebugHost(moduleId);
        docInfoDTO.setDebugEnvs(CopyUtil.copyList(debugEnvs, DebugHostDTO::new));
        List<DocParam> params = docParamService.list("doc_id", docInfo.getId());
        params.sort(Comparator.comparing(DocParam::getOrderIndex));
        Map<Byte, List<DocParam>> paramsMap = params.stream()
                .collect(Collectors.groupingBy(DocParam::getStyle));
        List<DocParam> pathParams = paramsMap.getOrDefault(ParamStyleEnum.PATH.getStyle(), Collections.emptyList());
        List<DocParam> headerParams = paramsMap.getOrDefault(ParamStyleEnum.HEADER.getStyle(), Collections.emptyList());
        List<DocParam> queryParams = paramsMap.getOrDefault(ParamStyleEnum.QUERY.getStyle(), Collections.emptyList());
        List<DocParam> requestParams = paramsMap.getOrDefault(ParamStyleEnum.REQUEST.getStyle(), Collections.emptyList());
        List<DocParam> responseParams = paramsMap.getOrDefault(ParamStyleEnum.RESPONSE.getStyle(), Collections.emptyList());
        List<DocParam> errorCodeParams = paramsMap.getOrDefault(ParamStyleEnum.ERROR_CODE.getStyle(), new ArrayList<>(0));
        docInfoDTO.setPathParams(CopyUtil.copyList(pathParams, DocParamDTO::new));
        docInfoDTO.setHeaderParams(CopyUtil.copyList(headerParams, DocParamDTO::new));
        docInfoDTO.setQueryParams(CopyUtil.copyList(queryParams, DocParamDTO::new));
        docInfoDTO.setRequestParams(CopyUtil.copyList(requestParams, DocParamDTO::new));
        docInfoDTO.setResponseParams(CopyUtil.copyList(responseParams, DocParamDTO::new));
        docInfoDTO.setErrorCodeParams(CopyUtil.copyList(errorCodeParams, DocParamDTO::new));
        return docInfoDTO;
    }


    public List<DocInfoDTO> listDocDetail(Collection<Long> docIdList) {
        if (CollectionUtils.isEmpty(docIdList)) {
            return Collections.emptyList();
        }
        Query query = new Query()
                .in("id", docIdList);
        List<DocInfo> docInfos = this.list(query);
        return docInfos.stream()
                .sorted(Comparator.comparing(DocInfo::getOrderIndex))
                .map(this::getDocDetail)
                .collect(Collectors.toList());
    }

    public List<DocInfo> listDocByIds(Collection<Long> docIdList) {
        if (CollectionUtils.isEmpty(docIdList)) {
            return Collections.emptyList();
        }
        Query query = new Query()
                .in("id", docIdList);
        return this.list(query);
    }


    private DocInfoDTO getDocDetail(DocInfo docInfo) {
        DocInfoDTO docInfoDTO = this.getDocInfoDTO(docInfo);
        Long moduleId = docInfoDTO.getModuleId();
        List<DocParam> globalHeaders = moduleConfigService.listGlobalHeaders(moduleId);
        List<DocParam> globalParams = moduleConfigService.listGlobalParams(moduleId);
        List<DocParam> globalReturns = moduleConfigService.listGlobalReturns(moduleId);
        List<DocParam> globalErrorCodes = listCommonErrorCodes(moduleId);
        docInfoDTO.setGlobalHeaders(CopyUtil.copyList(globalHeaders, DocParamDTO::new));
        docInfoDTO.setGlobalParams(CopyUtil.copyList(globalParams, DocParamDTO::new));
        docInfoDTO.setGlobalReturns(CopyUtil.copyList(globalReturns, DocParamDTO::new));
        docInfoDTO.getErrorCodeParams().addAll(CopyUtil.copyList(globalErrorCodes, DocParamDTO::new));
        docInfoDTO.getGlobalHeaders().forEach(docParamDTO -> docParamDTO.setGlobal(true));
        return docInfoDTO;
    }

    private List<DocParam> listCommonErrorCodes(long moduleId) {
        return moduleConfigService.listCommonErrorCodes(moduleId);
    }

    /**
     * ??????????????????
     * @param docInfoDTO ????????????
     * @param user ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized DocInfo saveDocInfo(DocInfoDTO docInfoDTO, User user) {
        return doSaveDocInfo(docInfoDTO, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized DocInfo updateDocInfo(DocInfoDTO docInfoDTO, User user) {
        return doUpdateDocInfo(docInfoDTO, user);
    }

    public DocInfo doPushSaveDocInfo(DocInfoDTO docInfoDTO, User user) {
        // ??????????????????
        DocInfo docInfo = this.insertDocInfo(docInfoDTO, user);
        // ????????????
        this.doUpdateParams(docInfo, docInfoDTO, user);
        return docInfo;
    }

    public List<DocMeta> listDocMeta(long moduleId) {
        Query query = new Query().eq("module_id", moduleId);
        return this.getMapper().listBySpecifiedColumns(Arrays.asList("data_id", "is_locked", "md5"), query, DocMeta.class);
    }

    public static boolean isLocked(String dataId, List<DocMeta> docMetas) {
        if (CollectionUtils.isEmpty(docMetas)) {
            return false;
        }
        for (DocMeta docMeta : docMetas) {
            if (Objects.equals(dataId, docMeta.getDataId()) && docMeta.getIsLocked() == Booleans.TRUE) {
                return true;
            }
        }
        return false;
    }

    public static boolean isContentChanged(String dataId, String newMd5, List<DocMeta> docMetas) {
        if (CollectionUtils.isEmpty(docMetas)) {
            return false;
        }
        for (DocMeta docMeta : docMetas) {
            // ??????????????????
            if (StringUtils.isEmpty(docMeta.getMd5())) {
                continue;
            }
            if (Objects.equals(dataId, docMeta.getDataId()) && !Objects.equals(newMd5, docMeta.getMd5())) {
                return true;
            }
        }
        return false;
    }

    public DocInfo doSaveDocInfo(DocInfoDTO docInfoDTO, User user) {
        // ??????????????????
        DocInfo docInfo = this.saveBaseInfo(docInfoDTO, user);
        // ????????????
        this.doUpdateParams(docInfo, docInfoDTO, user);
        return docInfo;
    }

    public DocInfo doUpdateDocInfo(DocInfoDTO docInfoDTO, User user) {
        // ??????????????????
        DocInfo docInfo = this.modifyDocInfo(docInfoDTO, user);
        // ????????????
        this.doUpdateParams(docInfo, docInfoDTO, user);
        return docInfo;
    }

    private void doUpdateParams(DocInfo docInfo, DocInfoDTO docInfoDTO, User user) {
        docParamService.saveParams(docInfo, docInfoDTO.getPathParams(), ParamStyleEnum.PATH, user);
        docParamService.saveParams(docInfo, docInfoDTO.getHeaderParams(), ParamStyleEnum.HEADER, user);
        docParamService.saveParams(docInfo, docInfoDTO.getQueryParams(), ParamStyleEnum.QUERY, user);
        docParamService.saveParams(docInfo, docInfoDTO.getRequestParams(), ParamStyleEnum.REQUEST, user);
        docParamService.saveParams(docInfo, docInfoDTO.getResponseParams(), ParamStyleEnum.RESPONSE, user);
        docParamService.saveParams(docInfo, docInfoDTO.getErrorCodeParams(), ParamStyleEnum.ERROR_CODE, user);
    }

    private DocInfo saveBaseInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = this.insertDocInfo(docInfoDTO, user);
        // ???????????????
        userMessageService.sendMessageByModifyDoc(docInfo);
        return docInfo;
    }

    private DocInfo insertDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = buildDocInfo(docInfoDTO, user);
        this.getMapper().saveDocInfo(docInfo);
        return docInfo;
    }

    private DocInfo modifyDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = buildDocInfo(docInfoDTO, user);
        this.update(docInfo);
        return docInfo;
    }

    private DocInfo buildDocInfo(DocInfoDTO docInfoDTO, User user) {
        DocInfo docInfo = CopyUtil.copyBean(docInfoDTO, DocInfo::new);
        // ????????????
        docInfo.setCreateMode(user.getOperationModel());
        docInfo.setModifyMode(user.getOperationModel());
        docInfo.setCreatorId(user.getUserId());
        docInfo.setCreatorName(user.getNickname());
        docInfo.setModifierId(user.getUserId());
        docInfo.setModifierName(user.getNickname());
        docInfo.setDataId(docInfoDTO.buildDataId());
        if (docInfo.getDescription() == null) {
            docInfo.setDescription("");
        }
        return docInfo;
    }

    public DocInfo getByDataId(String dataId) {
        return get("data_id", dataId);
    }

    /**
     * ??????????????????????????????
     * @param moduleId ??????id
     * @return ????????????
     */
    public List<DocInfo> listFolders(long moduleId) {
        return this.listModuleDoc(moduleId)
                .stream()
                .filter(docInfo -> docInfo.getIsFolder() == Booleans.TRUE)
                .collect(Collectors.toList());
    }


    public boolean isExistFolderForUpdate(long id, String folderName, long moduleId, long parentId) {
        DocInfo docInfo = getByModuleIdAndParentIdAndName(moduleId, parentId, folderName);
        return docInfo != null && docInfo.getId() != id;
    }

    public DocInfo getByModuleIdAndParentIdAndName(long moduleId, long parentId, String name) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("parent_id", parentId)
                .eq("name", name);
        return get(query);
    }

    /**
     * ??????????????????
     *
     * @param id ??????id
     * @param name ????????????
     * @param user ?????????
     */
    public void updateDocFolderName(long id, String name, User user) {
        DocInfo folder = getById(id);
        Assert.notNull(folder, name + " ???????????????");
        Long moduleId = folder.getModuleId();
        if (isExistFolderForUpdate(id, name, moduleId, 0)) {
            throw new BizException(name + " ?????????");
        }
        folder.setName(name);
        folder.setModifyMode(user.getOperationModel());
        folder.setModifierId(user.getUserId());
        folder.setIsDeleted(Booleans.FALSE);
        this.update(folder);
    }

    /**
     * ????????????
     * @param id ????????????
     * @param user ??????
     */
    public void deleteDocInfo(long id, User user) {
        DocInfo docInfo = getById(id);
        docInfo.setModifyMode(user.getOperationModel());
        docInfo.setModifierId(user.getUserId());
        docInfo.setIsDeleted(Booleans.TRUE);
        this.userMessageService.sendMessageByDeleteDoc(docInfo);
        // ????????????dataId???????????????????????????
        docInfo.setDataId(IdGen.nextId());
        this.update(docInfo);
    }

    /**
     * ???????????????????????????????????????????????????????????????
     * @param folderName ????????????
     * @param moduleId ??????id
     * @param user ?????????
     */
    public DocInfo createDocFolder(String folderName, long moduleId, User user) {
        return createDocFolder(folderName,  moduleId, user, 0L);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param folderName ????????????
     * @param moduleId   ??????id
     * @param user       ?????????
     * @param parentId   ?????????id
     * @return ????????????????????????
     */
    public DocInfo createDocFolder(String folderName, long moduleId, User user, Long parentId) {
        DocFolderCreateDTO docFolderCreateDTO = new DocFolderCreateDTO();
        docFolderCreateDTO.setName(folderName);
        docFolderCreateDTO.setModuleId(moduleId);
        docFolderCreateDTO.setParentId(parentId);
        docFolderCreateDTO.setUser(user);
        docFolderCreateDTO.setDocTypeEnum(DocTypeEnum.HTTP);
        return createDocFolder(docFolderCreateDTO);
    }

    public DocInfo createDocFolder(DocFolderCreateDTO docFolderCreateDTO) {
        DocInfoDTO docInfoDTO = new DocInfoDTO();
        docInfoDTO.setName(docFolderCreateDTO.getName());
        docInfoDTO.setModuleId(docFolderCreateDTO.getModuleId());
        docInfoDTO.setParentId(docFolderCreateDTO.getParentId());
        if (docFolderCreateDTO.getDocTypeEnum() != null) {
            docInfoDTO.setType(docFolderCreateDTO.getDocTypeEnum().getType());
        }
        docInfoDTO.setIsFolder(Booleans.TRUE);
        docInfoDTO.setAuthor(docFolderCreateDTO.getAuthor());
        docInfoDTO.setOrderIndex(docFolderCreateDTO.getOrderIndex());
        DocInfo docInfo = insertDocInfo(docInfoDTO, docFolderCreateDTO.getUser());
        Map<String, ?> props = docFolderCreateDTO.getProps();
        propService.saveProps(props, docInfo.getId(), PropTypeEnum.DOC_INFO_PROP);
        return docInfo;
    }

    public DocInfo createDocItem(DocItemCreateDTO docItemCreateDTO) {
        User user = docItemCreateDTO.getUser();
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(docItemCreateDTO, DocInfoDTO::new);
        docInfoDTO.setIsDeleted(Booleans.FALSE);
        return insertDocInfo(docInfoDTO, user);
    }

    /**
     * ???????????????????????????
     * @param moduleId ??????id
     */
    public void deleteOpenAPIModuleDocs(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("create_mode", OperationMode.OPEN.getType())
                .eq("is_locked", Booleans.FALSE);
        // ???????????????id
        List<Long> idList = this.getMapper().listBySpecifiedColumns(Collections.singletonList("id"), query, Long.class);
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        // ????????????
        Query delQuery = new Query()
                .in("id", idList);
        // DELETE FROM doc_info WHERE id in (..)
        this.getMapper().deleteByQuery(delQuery);

        // ???????????????????????????
        Query paramDelQuery = new Query()
                .in("doc_id", idList)
                .eq("create_mode", OperationMode.OPEN.getType())
                ;
        // DELETE FROM doc_param WHERE doc_id in (..)
        docParamService.getMapper().deleteByQuery(paramDelQuery);
    }
}