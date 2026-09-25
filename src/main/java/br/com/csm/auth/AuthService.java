package br.com.csm.auth;

import br.com.csm.audit.dto.AuditMessage;
import br.com.csm.audit.enums.EventType;
import br.com.csm.audit.enums.LogLevel;
import br.com.csm.audit.message.AuditLogProducer;
import br.com.csm.auth.dto.AuthResponse;
import br.com.csm.auth.dto.LoginRequest;
import br.com.csm.auth.dto.TokenRefreshRequest;
import br.com.csm.auth.dto.TokenRefreshResponse;
import br.com.csm.core.exception.AuthenticationException;
import br.com.csm.permission.Permission;
import br.com.csm.role.Role;
import br.com.csm.user.User;
import br.com.csm.user.UserRepository;
import br.com.csm.core.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogProducer auditLogProducer;

    @Transactional
    public AuthResponse authenticate(LoginRequest request) {

        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new AuthenticationException("Usuário ou senha inválidos"));

        if (user.getStatus() == 0 || user.getDeletedAt() != null) {
            throw new AuthenticationException("Usuário inativo ou excluído do sistema.");
        }

        if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(OffsetDateTime.now())) {
            throw new AuthenticationException("Usuário temporariamente bloqueado por excesso de tentativas.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            handleFailedAttempt(user);
            throw new AuthenticationException("Usuário ou senha inválidos");
        }

        if (user.getFailedAttempts() > 0 || user.getBlockedUntil() != null) {
            user.setFailedAttempts(0);
            user.setBlockedUntil(null);
            userRepository.save(user);
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        AuthResponse.UserSummary userSummary = AuthResponse.UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .login(user.getLogin())
                .roles(roles)
                .permissions(permissions)
                .build();

        // 6. Gera o JWT (de 15 min)
        String token = tokenService.generateToken(user);

        // 7. Gera o Refresh Token No Redis (até então 7 dias)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        HttpServletRequest httpRequest = getHttpRequest();

        AuditMessage auditMessage = AuditMessage.builder()
                .timestamp(Instant.now()).systemOrigin("CSM-IAM")
                .traceId(UUID.randomUUID().toString())
                .logLevel(LogLevel.AUDIT)
                .eventType(EventType.SECURITY)
                .action("LOGIN_SUCCESS")
                .status("SUCCESS")
                .userId(user.getId())
                .username(user.getLogin())
                .ipAddress(getClientIp(httpRequest))
                .httpMethod(httpRequest != null ? httpRequest.getMethod() : "UNKNOWN")
                .endpoint(httpRequest != null ? httpRequest.getRequestURI() : "UNKNOWN")
                .payloadRequest("{\"login\": \"" + request.login() + "\"}")
                .build();

        auditLogProducer.sendLog(auditMessage);

        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(600)
                .user(userSummary)
                .build();
    }

    @Transactional
    public TokenRefreshResponse refreshToken (TokenRefreshRequest request) {

        RefreshToken refreshToken = refreshTokenService.verifyAndGetToken(request.refreshToken());

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new AuthenticationException("Usuário não encontrado ou sessão inválida."));

        if (user.getStatus() == 0 || user.getDeletedAt() != null) {
            throw new AuthenticationException("Usuário inativo ou excluído do sistema.");
        }

        String newAccessToken = tokenService.generateToken(user);


        // Possibilidade, caso eu queira renovar o Refresh Token a cada uso, gera um novo aqui (analisar)
        // RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
        // String refreshTokenValue = newRefreshToken.getToken();
        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken()) // Retorna o mesmo refresh token
                .tokenType("Bearer")
                .expiresIn(600) // 600 segundos (10 minutos)
                .build();
    }

    public User userInfo(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException("Usuário não encontrado ou sessão inválida"));
    }

    private void handleFailedAttempt(User user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);
        if (attempts >= 5) {
            user.setBlockedUntil(OffsetDateTime.now().plusMinutes(15));
        }

        userRepository.save(user);
    }

    private HttpServletRequest getHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
