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

import com.payflow.dto.TransferRequest;
import com.payflow.dto.TransferResponse;
import com.payflow.exception.InsufficientBalanceException;
import com.payflow.exception.InvalidTransferException;

import java.util.Objects;

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

    @Transactional
    public TransferResponse transfer(Long senderUserId, TransferRequest request) {

        Long receiverUserId = request.getReceiverUserId();
        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException(
                    "Transfer amount must be greater than zero");
        }

        // 1. Validate sender and receiver
        if (Objects.equals(senderUserId, receiverUserId)) {
            throw new InvalidTransferException(
                    "Sender and receiver cannot be the same user");
        }

        // 2. Lock wallets in ascending user ID order
        Long firstUserId = Math.min(senderUserId, receiverUserId);
        Long secondUserId = Math.max(senderUserId, receiverUserId);

        Wallet firstWallet = walletRepository.findByUserIdForUpdate(firstUserId)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet not found for user ID: " + firstUserId));

        Wallet secondWallet = walletRepository.findByUserIdForUpdate(secondUserId)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet not found for user ID: " + secondUserId));

        // 3. Identify sender and receiver wallets
        Wallet senderWallet = senderUserId.equals(firstUserId)
                ? firstWallet
                : secondWallet;

        Wallet receiverWallet = receiverUserId.equals(firstUserId)
                ? firstWallet
                : secondWallet;

        // 4. Check sufficient balance
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient wallet balance");
        }

        // 5. Calculate updated balances
        BigDecimal senderBalance = senderWallet.getBalance().subtract(amount);

        BigDecimal receiverBalance = receiverWallet.getBalance().add(amount);

        // 6. Update both wallets
        senderWallet.setBalance(senderBalance);
        receiverWallet.setBalance(receiverBalance);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 7. Create sender transaction record
        Transaction senderTransaction = new Transaction();
        senderTransaction.setWallet(senderWallet);
        senderTransaction.setRelatedWallet(receiverWallet);
        senderTransaction.setAmount(amount);
        senderTransaction.setType(TransactionType.TRANSFER);
        senderTransaction.setStatus(TransactionStatus.SUCCESS);
        senderTransaction.setDescription("Money transferred");

        // 8. Create receiver transaction record
        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setWallet(receiverWallet);
        receiverTransaction.setRelatedWallet(senderWallet);
        receiverTransaction.setAmount(amount);
        receiverTransaction.setType(TransactionType.TRANSFER);
        receiverTransaction.setStatus(TransactionStatus.SUCCESS);
        receiverTransaction.setDescription("Money received");

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);

        // 9. Return transfer response
        return new TransferResponse(
                senderTransaction.getId(),
                senderWallet.getId(),
                receiverWallet.getId(),
                amount,
                senderBalance,
                receiverBalance,
                TransactionType.TRANSFER.name(),
                TransactionStatus.SUCCESS.name(),
                senderTransaction.getCreatedAt());
    }
}