package com.specsense.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CrmFollowUp {
    private Long id;
    private Long inquiryId;
    private Long customerId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate followDate;
    private String method;
    private String content;
    private String result;
    private String attachments;
    private Long createdBy;
    private String createdByName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
