package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.message.Message;
import cn.torna.common.message.MessageEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.ThreadPoolUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.entity.UserMessage;
import cn.torna.dao.mapper.UserMessageMapper;
import cn.torna.service.dto.MessageDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.query.param.SchPageableParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wugang
 */
@Service
public class UserMessageService extends BaseService<UserMessage, UserMessageMapper> {

    @Autowired
    private UserSubscribeService userSubscribeService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${torna.message.max-length:250}")
    private int messageMaxLength;

    public List<UserMessage> listUserUnReadMessage(long userId, int limit) {
        Query query = new Query()
                .eq("user_id", userId)
                .eq("is_read", Booleans.FALSE)
                .orderby("id", Sort.DESC)
                .limit(0, limit);
        return this.list(query);
    }

    public PageEasyui<UserMessage> pageMessage(long userId, SchPageableParam pageableParam) {
        Query query = new Query()
                .eq("user_id", userId)
                .limit(pageableParam.getStart(), pageableParam.getLimit())
                .orderby("is_read", Sort.ASC)
                .orderby("id", Sort.DESC);

        return this.page(query);
    }

    public void setRead(long id) {
        UserMessage userMessage = this.getById(id);
        userMessage.setIsRead(Booleans.TRUE);
        this.update(userMessage);
    }

    /**
     * ??????????????????
     * @param userId
     */
    public void setReadAll(long userId) {
        this.getMapper().readAll(userId);
    }

    /**
     * ??????????????????????????????
     *
     * @param content ????????????
     */
    public void sendMessageToAdmin(MessageDTO messageDTO, String content) {
        List<Long> userIds = userInfoService.listSuperAdmin()
                .stream()
                .map(UserInfo::getId)
                .collect(Collectors.toList());
        this.sendMessage(userIds, messageDTO, content);
    }

    /**
     * ???????????????
     * @param userIds ??????
     * @param messageDTO messageDTO
     * @param params ????????????
     */
    public void sendMessage(List<Long> userIds, MessageDTO messageDTO, Object... params) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        ThreadPoolUtil.execute(() -> {
            MessageEnum messageEnum = messageDTO.getMessageEnum();
            Locale locale = messageDTO.getLocale();
            if (locale == null) {
                locale = Locale.SIMPLIFIED_CHINESE;
            }
            Message message = messageEnum.getMessageMeta().getMessage(locale, params);
            String content = message.getContent();
            if (content != null && content.length() > messageMaxLength) {
                content = content.substring(0, messageMaxLength);
            }
            String finalContent = content;
            List<UserMessage> userMessages = userIds.stream()
                    .map(userId -> {
                        UserMessage userMessage = new UserMessage();
                        userMessage.setUserId(userId);
                        userMessage.setMessage(finalContent);
                        userMessage.setIsRead(Booleans.FALSE);
                        userMessage.setType(messageDTO.getType().getType());
                        userMessage.setSourceId(messageDTO.getSourceId());
                        return userMessage;
                    })
                    .collect(Collectors.toList());

            this.saveBatch(userMessages);
        });
    }

    public void sendMessageByModifyDoc(DocInfo docInfo) {
        List<Long> finalUserIds = getSubscribeDocUserId(docInfo);
        if (finalUserIds.isEmpty()) {
            return;
        }
        String remark = docInfo.getRemark();
        MessageEnum messageEnum = StringUtils.isEmpty(remark) ? MessageEnum.DOC_UPDATE : MessageEnum.DOC_UPDATE_REMARK;
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageEnum(messageEnum);
        messageDTO.setType(UserSubscribeTypeEnum.DOC);
        messageDTO.setSourceId(docInfo.getId());
        messageDTO.setLocale(UserContext.getLocale());
        this.sendMessage(finalUserIds, messageDTO, docInfo.getModifierName(), docInfo.getName(), remark);
    }

    public void sendMessageByDeleteDoc(DocInfo docInfo) {
        List<Long> finalUserIds = getSubscribeDocUserId(docInfo);
        if (finalUserIds.isEmpty()) {
            return;
        }
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageEnum(MessageEnum.DOC_DELETE);
        messageDTO.setType(UserSubscribeTypeEnum.DOC);
        messageDTO.setSourceId(docInfo.getId());
        messageDTO.setLocale(UserContext.getLocale());
        this.sendMessage(finalUserIds, messageDTO, docInfo.getModifierName(), docInfo.getName());
    }

    private List<Long> getSubscribeDocUserId(DocInfo docInfo) {
        List<Long> userIds = userSubscribeService.listUserIds(UserSubscribeTypeEnum.DOC, docInfo.getId());
        return userIds.stream()
                .filter(userId -> !Objects.equals(docInfo.getModifierId(), userId))
                .collect(Collectors.toList());
    }

}