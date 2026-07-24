package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUser {
    private Long id;
    private String username;
    private String passwordHash;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
