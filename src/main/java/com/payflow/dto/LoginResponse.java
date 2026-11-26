
package com.payflow.dto;

public class LoginResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String token;
    private String tokenType;
    private String message;

    public LoginResponse(
            Long userId,
            String fullName,
            String email,
            String token,
            String tokenType,
            String message) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.token = token;
        this.tokenType = tokenType;
        this.message = message;
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

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getMessage() {
        return message;
    }
}
