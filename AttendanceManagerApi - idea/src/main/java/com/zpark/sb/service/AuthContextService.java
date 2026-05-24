package com.zpark.sb.service;

import com.zpark.sb.config.AuthInterceptor;
import com.zpark.sb.entity.Employee;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthContextService {

    private final EmployeeService employeeService;

    public AuthContextService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public String getCurrentUserNumber(HttpServletRequest request) {
        Object value = request.getAttribute(AuthInterceptor.CURRENT_USER_NUMBER_ATTR);
        return value == null ? null : value.toString();
    }

    public Employee getCurrentUser(HttpServletRequest request) {
        String number = getCurrentUserNumber(request);
        return number == null ? null : employeeService.findByNumber(number);
    }

    public boolean isAdmin(HttpServletRequest request) {
        Employee currentUser = getCurrentUser(request);
        return currentUser != null && "3".equals(currentUser.getType());
    }

    public boolean isSelfOrAdmin(HttpServletRequest request, String employeeNumber) {
        if (isAdmin(request)) {
            return true;
        }
        String currentUserNumber = getCurrentUserNumber(request);
        return currentUserNumber != null && currentUserNumber.equals(employeeNumber);
    }
}

