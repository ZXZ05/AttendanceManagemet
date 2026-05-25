package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.NotificationMessage;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private AuthContextService authContextService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/list")
    public Result list(
            @RequestParam(required = false) Boolean onlyUnread,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request
    ) {
        String currentUser = authContextService.getCurrentUserNumber(request);
        if (currentUser == null) {
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);
        }
        List<NotificationMessage> messages = notificationService.listByReceiver(currentUser, onlyUnread, limit);
        return Result.success(messages);
    }

    @GetMapping("/unreadCount")
    public Result unreadCount(HttpServletRequest request) {
        String currentUser = authContextService.getCurrentUserNumber(request);
        if (currentUser == null) {
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("count", notificationService.countUnread(currentUser));
        return Result.success(data);
    }

    @PostMapping("/read")
    public Result read(@RequestBody NotificationMessage message, HttpServletRequest request) {
        String currentUser = authContextService.getCurrentUserNumber(request);
        if (currentUser == null) {
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);
        }
        boolean updated = notificationService.markRead(message == null ? null : message.getId(), currentUser);
        if (!updated) {
            return Result.failure(ResultCode.DATA_IS_WRONG);
        }
        return Result.success();
    }

    @PostMapping("/readAll")
    public Result readAll(HttpServletRequest request) {
        String currentUser = authContextService.getCurrentUserNumber(request);
        if (currentUser == null) {
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);
        }
        int size = notificationService.markAllRead(currentUser);
        Map<String, Object> data = new HashMap<>();
        data.put("updated", size);
        return Result.success(data);
    }
}

