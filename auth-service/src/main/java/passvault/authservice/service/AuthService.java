package passvault.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import passvault.authservice.dto.*;
import passvault.authservice.event.EventPublisher;
import passvault.authservice.exception.TokenRefreshException;
import passvault.authservice.exception.UserAlreadyExistsException;
import passvault.authservice.model.RefreshToken;
import passvault.authservice.model.Role;
import passvault.authservice.model.User;
import passvault.authservice.repository.UserRepository;
import passvault.authservice.security.JwtTokenProvider;
import passvault.authservice.security.UserPrincipal;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final EventPublisher eventPublisher;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        // Create new user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        // Set default role
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);

        userRepository.save(user);

        // Publish event
        eventPublisher.publishUserRegistered(user);

        // Authenticate user after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                tokenProvider.getAccessTokenExpiration(),
                user.getUsername()
        );
    }

    public AuthResponse login(LoginRequest request) {
        return login(request, null);
    }

    public AuthResponse login(LoginRequest request, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);


        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                tokenProvider.getAccessTokenExpiration(),
                user.getUsername()
        );
    }

    @Transactional
    public MessageResponse logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        refreshTokenService.revokeAllUserTokens(user);
        SecurityContextHolder.clearContext();
        
        return MessageResponse.success("Successfully logged out");
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserPrincipal userPrincipal = UserPrincipal.create(user);
                    String accessToken = tokenProvider.generateAccessToken(userPrincipal);
                    
                    return new AuthResponse(
                            accessToken,
                            requestRefreshToken,
                            tokenProvider.getAccessTokenExpiration(),
                            user.getUsername()
                    );
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, 
                        "Refresh token is not in database"));
    }

    public TokenValidationResponse validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return new TokenValidationResponse(false, "Token is missing");
        }

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!tokenProvider.validateToken(token)) {
            return new TokenValidationResponse(false, "Token is invalid or expired");
        }

        String username = tokenProvider.getUsernameFromToken(token);
        UUID userId = tokenProvider.getUserIdFromToken(token);
        String rolesString = tokenProvider.getRolesFromToken(token);
        
        Set<String> roles = Set.of(rolesString.split(","));

        return new TokenValidationResponse(true, username, userId, roles);
    }
}
