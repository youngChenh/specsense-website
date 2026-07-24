package com.specsense.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContactInquiry {
    private Long id;
    private String name;
    private String email;
    private String company;
    private String product;
    private String message;
    private LocalDateTime createdAt;
}
