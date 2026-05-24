package com.zpark.sb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpark.sb.service.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER_NUMBER_ATTR = "currentUserNumber";

    private final AuthTokenService authTokenService;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(AuthTokenService authTokenService, ObjectMapper objectMapper) {
        this.authTokenService = authTokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = extractToken(request);
        if (token == null) {
            writeUnauthorized(response, Result.failure(ResultCode.USER_NOT_LOGGED_IN));
            return false;
        }

        String employeeNumber = authTokenService.resolveEmployeeNumber(token);
        if (employeeNumber == null) {
            writeUnauthorized(response, Result.failure(ResultCode.TOKEN_INVALID));
            return false;
        }

        request.setAttribute(CURRENT_USER_NUMBER_ATTR, employeeNumber);
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        String token = request.getHeader("X-Auth-Token");
        if (token != null && !token.trim().isEmpty()) {
            return token.trim();
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response, Result result) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

