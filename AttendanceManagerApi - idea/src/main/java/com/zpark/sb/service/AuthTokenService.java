package com.zpark.sb.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthTokenService {

    private static final long TOKEN_TTL_MILLIS = 8 * 60 * 60 * 1000L;

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    public String createToken(String employeeNumber) {
        cleanupExpiredSessions();
        String token = UUID.randomUUID().toString().replace("-", "");
        long expiresAt = Instant.now().toEpochMilli() + TOKEN_TTL_MILLIS;
        sessions.put(token, new SessionInfo(employeeNumber, expiresAt));
        return token;
    }

    public String resolveEmployeeNumber(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        SessionInfo sessionInfo = sessions.get(token);
        if (sessionInfo == null) {
            return null;
        }
        if (sessionInfo.expiresAt < Instant.now().toEpochMilli()) {
            sessions.remove(token);
            return null;
        }
        return sessionInfo.employeeNumber;
    }

    private void cleanupExpiredSessions() {
        long now = Instant.now().toEpochMilli();
        sessions.entrySet().removeIf(entry -> entry.getValue().expiresAt < now);
    }

    private static final class SessionInfo {
        private final String employeeNumber;
        private final long expiresAt;

        private SessionInfo(String employeeNumber, long expiresAt) {
            this.employeeNumber = employeeNumber;
            this.expiresAt = expiresAt;
        }
    }
}

