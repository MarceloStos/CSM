package br.com.csm.service;

import br.com.csm.dto.AuthDTO;
import br.com.csm.exception.AuthenticationException;
import br.com.csm.model.Permission;
import br.com.csm.model.Role;
import br.com.csm.model.User;
import br.com.csm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder; // <- Usaremos o PasswordEncoder direto!

    @Transactional
    public AuthDTO.AuthResponse authenticate(AuthDTO.LoginRequest request) {

        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new AuthenticationException("Usuário ou senha inválidos"));

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

        Set<String> roles = user.getRole().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRole().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        AuthDTO.AuthResponse.UserSummary userSummary = AuthDTO.AuthResponse.UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .login(user.getLogin())
                .roles(roles)
                .permissions(permissions)
                .build();

        // 6. Gera o Token e devolve o DTO
        String token = tokenService.generateToken(user);

        return AuthDTO.AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(900)
                .user(userSummary)
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
}
