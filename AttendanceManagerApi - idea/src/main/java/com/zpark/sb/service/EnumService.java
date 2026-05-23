package com.zpark.sb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class EnumService {

    private static final Logger log = LoggerFactory.getLogger(EnumService.class);
    private static final Pattern TIME_PATTERN = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");

    @Value("${attendance.on-time}")
    private String onTime;

    @Value("${attendance.off-time}")
    private String offTime;

    public String getAttendanceTime() {
        if (onTime == null || offTime == null) {
            log.warn("attendance configuration missing in application.yaml");
            return "08:30-17:30";
        }
        if (!TIME_PATTERN.matcher(onTime).matches() || !TIME_PATTERN.matcher(offTime).matches()) {
            log.warn("attendance configuration invalid: onTime={}, offTime={}", onTime, offTime);
            return "08:30-17:30";
        }
        return String.format("%s-%s", onTime, offTime);
    }
}
