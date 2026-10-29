package com.specsense.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CrmAfterSale {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long orderId;
    private String orderNo;
    private Long ownerId;
    private String ownerName;
    private String supplierName;
    private String model;
    private String problem;
    private Integer resolved;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate resolvedAt;
    private Integer needChangeSupplier;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
