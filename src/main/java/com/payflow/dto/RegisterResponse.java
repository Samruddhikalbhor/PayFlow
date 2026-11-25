
package com.payflow.dto;

import java.time.LocalDateTime;

public class RegisterResponse {

    private Long userId;
    private String fullName;
    private String email;
    private Long walletId;
    private String message;
    private LocalDateTime createdAt;

    public RegisterResponse() {
    }

    public RegisterResponse(Long userId, String fullName, String email,
                            Long walletId, String message,
                            LocalDateTime createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.walletId = walletId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Long getWalletId() {
        return walletId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
