package com.swarajya.milktracker.service;

import com.swarajya.milktracker.dto.request.CreateUserRequest;
import com.swarajya.milktracker.dto.request.LoginRequest;
import com.swarajya.milktracker.dto.response.LoginResponse;
import com.swarajya.milktracker.dto.response.UserResponse;
import com.swarajya.milktracker.entity.RefreshToken;
import com.swarajya.milktracker.entity.User;
import com.swarajya.milktracker.entity.UserSettings;
import com.swarajya.milktracker.exception.DuplicateEmailException;
import com.swarajya.milktracker.exception.UserNotFoundException;
import com.swarajya.milktracker.repository.UserRepository;
import com.swarajya.milktracker.repository.UserSettingsRepository;
import com.swarajya.milktracker.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserSettingsRepository userSettingsRepository;
    @Value("${milk.default-price-per-liter}")
    private BigDecimal defaultPricePerLiter;


    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           RefreshTokenService refreshTokenService,
                           UserSettingsRepository userSettingsRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check whether email already exists
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                    "User with email '" + email + "' already exists"
            );
        }

        User user = new User();

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPhoneNumber(request.getPhoneNumber());

        // NEVER store the raw password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        User savedUser = userRepository.save(user);
        UserSettings settings = new UserSettings();
        settings.setUser(savedUser);
        settings.setDefaultPricePerLiter(defaultPricePerLiter);

        userSettingsRepository.save(settings);
        return mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + uuid));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() ->
                new UserNotFoundException(
                        "User not found"
                )
        );

        return mapToUserResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User with email '" + email + "' not found"
                        )
                );

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("USER")
                        .build();

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                900000
        );
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(
            String rawRefreshToken
    ) {

        RefreshToken oldRefreshToken =
                refreshTokenService.validateRefreshToken(
                        rawRefreshToken
                );

        User user = oldRefreshToken.getUser();

        // Revoke old refresh token
        refreshTokenService.revokeToken(
                oldRefreshToken
        );

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("USER")
                        .build();

        // Generate new access token
        String newAccessToken =
                jwtService.generateToken(userDetails);

        // Generate new refresh token
        String newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                900000
        );
    }


    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getUuid());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
