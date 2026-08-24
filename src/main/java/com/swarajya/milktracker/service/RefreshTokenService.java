package com.swarajya.milktracker.service;

import com.swarajya.milktracker.entity.RefreshToken;
import com.swarajya.milktracker.entity.User;
import com.swarajya.milktracker.exception.InvalidRefreshTokenException;
import com.swarajya.milktracker.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpiration;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh-expiration}")
            long refreshTokenExpiration
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Transactional
    public String createRefreshToken(User user) {

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(
                LocalDateTime.now()
                        .plusNanos(
                                refreshTokenExpiration * 1_000_000
                        )
        );
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public RefreshToken validateRefreshToken(
            String rawToken
    ) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid refresh token"
                                )
                        );

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token has been revoked"
            );
        }

        if (refreshToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new InvalidRefreshTokenException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(RefreshToken refreshToken) {

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return bytesToHex(hash);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    exception
            );
        }
    }

    private String bytesToHex(byte[] bytes) {

        StringBuilder result =
                new StringBuilder();

        for (byte b : bytes) {
            result.append(
                    String.format("%02x", b)
            );
        }

        return result.toString();
    }

    @Transactional
    public void revokeTokenByRawToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid or expired refresh token"
                                )
                        );

        if (refreshToken.isRevoked()) {
            return;
        }

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void logout(
            String rawToken,
            String authenticatedEmail
    ) {

        String tokenHash = hashToken(rawToken);

        refreshTokenRepository
                .findByTokenHash(tokenHash)
                .ifPresent(refreshToken -> {

                    String tokenUserEmail =
                            refreshToken.getUser().getEmail();

                    if (!tokenUserEmail.equalsIgnoreCase(
                            authenticatedEmail
                    )) {
                        throw new InvalidRefreshTokenException(
                                "Invalid refresh token"
                        );
                    }

                    if (!refreshToken.isRevoked()) {
                        refreshToken.setRevoked(true);
                        refreshTokenRepository.save(
                                refreshToken
                        );
                    }
                });
    }
}