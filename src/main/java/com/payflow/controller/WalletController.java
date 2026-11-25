package com.payflow.controller;

import com.payflow.dto.DepositRequest;
import com.payflow.dto.DepositResponse;
import com.payflow.service.WalletService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable Long userId,
            @Valid @RequestBody DepositRequest request) {

        DepositResponse response = walletService.deposit(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}