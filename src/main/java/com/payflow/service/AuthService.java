package com.payflow.service;

import com.payflow.dto.RegisterRequest;
import com.payflow.dto.RegisterResponse;
import com.payflow.entity.User;
import com.payflow.entity.Wallet;
import com.payflow.exception.EmailAlreadyExistsException;
import com.payflow.repository.UserRepository;
import com.payflow.repository.WalletRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payflow.dto.LoginRequest;
import com.payflow.dto.LoginResponse;
import com.payflow.exception.InvalidCredentialsException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // 1. Normalize email
        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // 2. Check whether email already exists
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(
                    "An account with this email already exists");
        }

        // 3. Check BCrypt's 72-byte input limit
        int passwordBytes = request.getPassword()
                .getBytes(StandardCharsets.UTF_8).length;

        if (passwordBytes > 72) {
            throw new IllegalArgumentException(
                    "Password must not exceed 72 bytes");
        }

        // 4. Create user
        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);

        // Never store the original password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 5. Save user to generate user ID
        User savedUser = userRepository.save(user);

        // 6. Create a wallet for the registered user
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);

        Wallet savedWallet = walletRepository.save(wallet);

        // 7. Return response without exposing the password
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedWallet.getId(),
                "Registration successful",
                savedUser.getCreatedAt());
    }

    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(java.util.Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                token,
                "Bearer",
                "Login successful");
    }

}