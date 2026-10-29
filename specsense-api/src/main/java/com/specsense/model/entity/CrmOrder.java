package com.specsense.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CrmOrder {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long inquiryId;
    private Long ownerId;
    private String ownerName;
    private String orderNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;
    private String supplierName;
    private String productName;
    private String model;
    private BigDecimal quantity;
    private String currency;
    private BigDecimal purchasePrice;
    private BigDecimal sellPrice;
    private String incoterm;
    private BigDecimal freight;
    private BigDecimal insurance;
    private BigDecimal bankFee;
    private Integer taxIncluded;
    private String invoiceType;
    private BigDecimal grossProfit;
    private BigDecimal grossMargin;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
