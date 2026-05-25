package com.zpark.sb.service;

import com.zpark.sb.dao.LoginRecordDao;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.LoginRecord;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class LoginRecordService {

    @Autowired
    private LoginRecordDao loginRecordDao;

    public void recordSuccess(Employee employee, HttpServletRequest request) {
        if (employee == null || isBlank(employee.getNumber())) {
            return;
        }
        LoginRecord record = new LoginRecord();
        record.setId(UUID.randomUUID().toString());
        record.setEmployeeNumber(employee.getNumber());
        record.setEmployeeName(employee.getName());
        record.setLoginStatus("SUCCESS");
        record.setIpAddress(resolveIpAddress(request));
        record.setUserAgent(trimToMax(request == null ? null : request.getHeader("User-Agent"), 500));
        record.setLoginTime(new Date());
        loginRecordDao.insert(record);
    }

    public List<LoginRecord> listByEmployeeNumber(String employeeNumber, Integer limit) {
        if (isBlank(employeeNumber)) {
            return List.of();
        }
        int size = limit == null || limit <= 0 ? 20 : Math.min(limit, 100);
        return loginRecordDao.listByEmployeeNumber(employeeNumber.trim(), size);
    }

    private String resolveIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String[] headerNames = new String[] {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_CLIENT_IP"
        };
        for (String headerName : headerNames) {
            String value = request.getHeader(headerName);
            if (!isBlank(value) && !"unknown".equalsIgnoreCase(value)) {
                String first = value.split(",")[0];
                return trimToMax(first, 64);
            }
        }
        return trimToMax(request.getRemoteAddr(), 64);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToMax(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }
}

