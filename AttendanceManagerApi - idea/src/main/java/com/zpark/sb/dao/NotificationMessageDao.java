package com.zpark.sb.dao;

import com.zpark.sb.entity.NotificationMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface NotificationMessageDao {
    int insert(NotificationMessage record);

    NotificationMessage selectById(String id);

    List<NotificationMessage> findByReceiver(NotificationMessage query);

    int countUnread(@Param("receiverNumber") String receiverNumber);

    int markRead(@Param("id") String id, @Param("receiverNumber") String receiverNumber, @Param("readTime") Date readTime);

    int markAllRead(@Param("receiverNumber") String receiverNumber, @Param("readTime") Date readTime);
}

