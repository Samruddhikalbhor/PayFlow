package com.payflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DepositResponse {

    private Long transactionId;
    private Long walletId;
    private BigDecimal depositedAmount;
    private BigDecimal updatedBalance;
    private String transactionType;
    private String status;
    private LocalDateTime createdAt;

    public DepositResponse(
            Long transactionId,
            Long walletId,
            BigDecimal depositedAmount,
            BigDecimal updatedBalance,
            String transactionType,
            String status,
            LocalDateTime createdAt) {

        this.transactionId = transactionId;
        this.walletId = walletId;
        this.depositedAmount = depositedAmount;
        this.updatedBalance = updatedBalance;
        this.transactionType = transactionType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public BigDecimal getDepositedAmount() {
        return depositedAmount;
    }

    public BigDecimal getUpdatedBalance() {
        return updatedBalance;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}