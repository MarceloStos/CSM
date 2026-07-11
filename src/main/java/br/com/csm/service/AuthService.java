package br.com.csm.service;

import br.com.csm.exception.AuthenticationException;
import br.com.csm.model.User;
import br.com.csm.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public User authenticate(String login, String rawPassword){

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos"));

        if (user.getStatus() == 0) {
            throw new AuthenticationException("Conta suspensa. Entre em contato com a administração");
        }

        boolean passwordMatches = rawPassword.equals(user.getPasswordHash());

        if (!passwordMatches){
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
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    private void handleFailedAttempt(User user) {
        int attemps = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attemps);
        if (attemps >= 5) {
            user.setBlockedUntil(OffsetDateTime.now().plusMinutes(15));
        }

        userRepository.save(user);
    }
}
