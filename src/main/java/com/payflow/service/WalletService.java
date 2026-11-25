package com.payflow.service;

import com.payflow.dto.DepositRequest;
import com.payflow.dto.DepositResponse;
import com.payflow.entity.Transaction;
import com.payflow.entity.TransactionStatus;
import com.payflow.entity.TransactionType;
import com.payflow.entity.Wallet;
import com.payflow.exception.WalletNotFoundException;
import com.payflow.repository.TransactionRepository;
import com.payflow.repository.WalletRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payflow.dto.TransactionHistoryResponse;
import java.util.List;
import java.util.stream.Collectors;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public DepositResponse deposit(Long userId, DepositRequest request) {

        // 1. Find and lock the user's wallet
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet not found for user ID: " + userId));

        // 2. Get the deposit amount
        BigDecimal amount = request.getAmount();

        // 3. Calculate the updated balance
        BigDecimal updatedBalance = wallet.getBalance().add(amount);

        // 4. Update the wallet balance
        wallet.setBalance(updatedBalance);

        walletRepository.save(wallet);

        // 5. Create a transaction record
        Transaction transaction = new Transaction();

        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription("Wallet deposit");

        Transaction savedTransaction = transactionRepository.save(transaction);

        // 6. Return the deposit response
        return new DepositResponse(
                savedTransaction.getId(),
                wallet.getId(),
                amount,
                updatedBalance,
                savedTransaction.getType().name(),
                savedTransaction.getStatus().name(),
                savedTransaction.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getTransactionHistory(Long userId) {

        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet not found for user ID: " + userId));

        List<Transaction> transactions = transactionRepository.findByWallet_IdOrderByCreatedAtDesc(
                wallet.getId());

        return transactions.stream()
                .map(transaction -> new TransactionHistoryResponse(
                        transaction.getId(),
                        wallet.getId(),
                        transaction.getAmount(),
                        transaction.getType().name(),
                        transaction.getStatus().name(),
                        transaction.getDescription(),
                        transaction.getCreatedAt()))
                .collect(Collectors.toList());
    }
}