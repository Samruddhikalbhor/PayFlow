
package com.payflow.controller;

import com.payflow.dto.TransferRequest;
import com.payflow.dto.TransferResponse;
import com.payflow.entity.User;
import com.payflow.service.WalletService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final WalletService walletService;

    public TransferController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal User authenticatedUser) {

        Long senderUserId = authenticatedUser.getId();

        TransferResponse response =
                walletService.transfer(senderUserId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
