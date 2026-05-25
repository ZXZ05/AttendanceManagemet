package com.zpark.sb.service;

import com.zpark.sb.dao.NotificationMessageDao;
import com.zpark.sb.entity.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private NotificationMessageDao notificationMessageDao;

    public void createNotice(String receiverNumber, String title, String content, String type, String relatedType, String relatedId) {
        if (isBlank(receiverNumber)) {
            return;
        }
        NotificationMessage message = new NotificationMessage();
        message.setId(UUID.randomUUID().toString());
        message.setReceiverNumber(receiverNumber);
        message.setTitle(trimToDefault(title, "系统提醒"));
        message.setContent(trimToDefault(content, ""));
        message.setType(trimToDefault(type, "SYSTEM"));
        message.setRelatedType(relatedType);
        message.setRelatedId(relatedId);
        message.setStatus("0");
        message.setCreatedTime(new Date());
        notificationMessageDao.insert(message);
    }

    public void createBatchNotice(List<String> receiverNumbers, String title, String content, String type, String relatedType, String relatedId) {
        if (receiverNumbers == null || receiverNumbers.isEmpty()) {
            return;
        }
        Set<String> receivers = new LinkedHashSet<>();
        for (String receiver : receiverNumbers) {
            if (!isBlank(receiver)) {
                receivers.add(receiver.trim());
            }
        }
        for (String receiver : receivers) {
            createNotice(receiver, title, content, type, relatedType, relatedId);
        }
    }

    public List<NotificationMessage> listByReceiver(String receiverNumber, Boolean onlyUnread, Integer limit) {
        NotificationMessage query = new NotificationMessage();
        query.setReceiverNumber(receiverNumber);
        query.setStatus(Boolean.TRUE.equals(onlyUnread) ? "0" : null);
        query.setLimit(limit == null || limit <= 0 ? 30 : Math.min(limit, 200));
        return notificationMessageDao.findByReceiver(query);
    }

    public int countUnread(String receiverNumber) {
        return notificationMessageDao.countUnread(receiverNumber);
    }

    public boolean markRead(String id, String receiverNumber) {
        if (isBlank(id) || isBlank(receiverNumber)) {
            return false;
        }
        return notificationMessageDao.markRead(id.trim(), receiverNumber.trim(), new Date()) > 0;
    }

    public int markAllRead(String receiverNumber) {
        if (isBlank(receiverNumber)) {
            return 0;
        }
        return notificationMessageDao.markAllRead(receiverNumber.trim(), new Date());
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private String trimToDefault(String text, String defaultValue) {
        return isBlank(text) ? defaultValue : text.trim();
    }
}

