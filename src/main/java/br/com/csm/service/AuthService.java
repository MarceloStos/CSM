package br.com.csm.service;

import br.com.csm.exception.AuthenticationException;
import br.com.csm.model.User;
import br.com.csm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User authenticate(String login, String rawPassword){

        User user = userRepository.findActiveAndUnblockedUser(login, 1, OffsetDateTime.now())
                .orElseThrow(() -> new AuthenticationException("Credenciais inválidas ou conta inativa/bloqueada"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            handleFailedAttempt(user);
            throw new AuthenticationException("Usuário ou senha inválidos");
        }

        user.setFailedAttempts(0);
        user.setBlockedUntil(null);
        user.setLastLogin(OffsetDateTime.now());

        return userRepository.save(user);
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
