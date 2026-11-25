package com.payflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionHistoryResponse {

    private Long transactionId;
    private Long walletId;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public TransactionHistoryResponse(
            Long transactionId,
            Long walletId,
            BigDecimal amount,
            String transactionType,
            String status,
            String description,
            LocalDateTime createdAt) {

        this.transactionId = transactionId;
        this.walletId = walletId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}