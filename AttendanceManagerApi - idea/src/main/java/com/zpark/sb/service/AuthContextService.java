package com.zpark.sb.service;

import com.zpark.sb.config.AuthInterceptor;
import com.zpark.sb.entity.Employee;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthContextService {

    public static final String ROLE_SUPERVISOR = "2";
    public static final String ROLE_SYS_ADMIN = "3";
    public static final String ROLE_EMPLOYEE = "4";
    public static final String ROLE_HR = "5";
    public static final String ROLE_FINANCE = "6";

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
        return currentUser != null && ROLE_SYS_ADMIN.equals(currentUser.getType());
    }

    public boolean hasRole(HttpServletRequest request, String... roles) {
        Employee currentUser = getCurrentUser(request);
        if (currentUser == null || currentUser.getType() == null || roles == null) {
            return false;
        }
        for (String role : roles) {
            if (role != null && role.equals(currentUser.getType())) {
                return true;
            }
        }
        return false;
    }

    public boolean canAccessAdminPortal(HttpServletRequest request) {
        return hasRole(request, ROLE_SUPERVISOR, ROLE_HR, ROLE_FINANCE, ROLE_SYS_ADMIN);
    }

    public boolean canManageEmployee(HttpServletRequest request) {
        return hasRole(request, ROLE_HR, ROLE_SYS_ADMIN);
    }

    public boolean canManageOrg(HttpServletRequest request) {
        return hasRole(request, ROLE_HR, ROLE_SYS_ADMIN);
    }

    public boolean canManageFinance(HttpServletRequest request) {
        return hasRole(request, ROLE_FINANCE, ROLE_SYS_ADMIN);
    }

    public boolean canViewStatistics(HttpServletRequest request) {
        return hasRole(request, ROLE_SUPERVISOR, ROLE_HR, ROLE_FINANCE, ROLE_SYS_ADMIN);
    }

    public boolean canManageAnnouncement(HttpServletRequest request) {
        return hasRole(request, ROLE_SUPERVISOR, ROLE_HR, ROLE_FINANCE, ROLE_SYS_ADMIN);
    }

    public boolean isSelfOrAdmin(HttpServletRequest request, String employeeNumber) {
        if (isAdmin(request)) {
            return true;
        }
        String currentUserNumber = getCurrentUserNumber(request);
        return currentUserNumber != null && currentUserNumber.equals(employeeNumber);
    }
}
