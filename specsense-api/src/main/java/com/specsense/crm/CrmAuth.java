package com.specsense.crm;

import com.specsense.config.JwtConfig;
import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CrmAuth {

    @Autowired
    private JwtConfig jwtConfig;

    @Data
    public static class User {
        private Long userId;
        private String username;
        private String role;

        public boolean isManager() {
            return "manager".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role);
        }

        public boolean isSales() {
            return "sales".equalsIgnoreCase(role);
        }

        public Long scopedOwnerId() {
            return isManager() ? null : userId;
        }
    }

    public User requireUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException(401, "Unauthorized");
        }
        try {
            Claims claims = jwtConfig.parseToken(authHeader.substring(7));
            User user = new User();
            Object userId = claims.get("userId");
            if (userId instanceof Number) {
                user.setUserId(((Number) userId).longValue());
            } else if (userId != null) {
                user.setUserId(Long.parseLong(userId.toString()));
            }
            user.setUsername(claims.getSubject());
            Object role = claims.get("role");
            user.setRole(role == null ? "sales" : role.toString());
            return user;
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException(401, "Unauthorized");
        }
    }

    public User requireManager(HttpServletRequest request) {
        User user = requireUser(request);
        if (!user.isManager()) {
            throw new AuthException(403, "仅管理层可操作");
        }
        return user;
    }

    public static class AuthException extends RuntimeException {
        private final int code;

        public AuthException(int code, String message) {
            super(message);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
