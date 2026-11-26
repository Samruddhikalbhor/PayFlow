
package com.payflow.controller;

import com.payflow.dto.DepositRequest;
import com.payflow.dto.DepositResponse;
import com.payflow.dto.TransactionHistoryResponse;
import com.payflow.entity.User;
import com.payflow.service.WalletService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody DepositRequest request) {

        Long userId = authenticatedUser.getId();

        DepositResponse response = walletService.deposit(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @AuthenticationPrincipal User authenticatedUser) {

        Long userId = authenticatedUser.getId();

        List<TransactionHistoryResponse> transactions =
                walletService.getTransactionHistory(userId);

        return ResponseEntity.ok(transactions);
    }
}
