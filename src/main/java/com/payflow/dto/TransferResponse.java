package com.payflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferResponse {

    private Long transactionId;
    private Long senderWalletId;
    private Long receiverWalletId;
    private BigDecimal transferredAmount;
    private BigDecimal senderUpdatedBalance;
    private BigDecimal receiverUpdatedBalance;
    private String transactionType;
    private String status;
    private LocalDateTime createdAt;

    public TransferResponse(
            Long transactionId,
            Long senderWalletId,
            Long receiverWalletId,
            BigDecimal transferredAmount,
            BigDecimal senderUpdatedBalance,
            BigDecimal receiverUpdatedBalance,
            String transactionType,
            String status,
            LocalDateTime createdAt) {

        this.transactionId = transactionId;
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.transferredAmount = transferredAmount;
        this.senderUpdatedBalance = senderUpdatedBalance;
        this.receiverUpdatedBalance = receiverUpdatedBalance;
        this.transactionType = transactionType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getSenderWalletId() {
        return senderWalletId;
    }

    public Long getReceiverWalletId() {
        return receiverWalletId;
    }

    public BigDecimal getTransferredAmount() {
        return transferredAmount;
    }

    public BigDecimal getSenderUpdatedBalance() {
        return senderUpdatedBalance;
    }

    public BigDecimal getReceiverUpdatedBalance() {
        return receiverUpdatedBalance;
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