package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private AuthContextService authContextService;
    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/overview")
    public Result overview(@RequestBody(required = false) Map<String, Object> payload, HttpServletRequest request) {
        if (!authContextService.canViewStatistics(request)) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }
        String month = payload == null ? null : toText(payload.get("month"));
        Integer months = payload == null ? null : toInteger(payload.get("months"));
        return Result.success(statisticsService.getOverview(month, months));
    }

    private String toText(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (Exception ignore) {
            return null;
        }
    }
}

