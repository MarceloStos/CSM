package br.com.csm.service;

import br.com.csm.auth.AuthService;
import br.com.csm.auth.dto.AuthResponse;
import br.com.csm.auth.dto.LoginRequest;
import br.com.csm.core.security.TokenService;
import br.com.csm.user.User;
import br.com.csm.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Deve bloquear o usuário por 15 minutos após a 5° tentativa falha de login")
    void shouldBlockUserAfterFiveFailedAttempts() {

        // Arrange (preparacao)
        String login = "marcelo.santos";
        String rawPassword = "senha_errada";

        User mockUser = new User();
        mockUser.setLogin(login);
        mockUser.setPasswordHash("senha_valida");
        mockUser.setFailedAttempts(4);
        mockUser.setBlockedUntil(null);

        LoginRequest request = new LoginRequest(login, rawPassword);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, mockUser.getPasswordHash())).thenReturn(false);

        // Act (Acao)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(request);
        });

        assertEquals("Usuário ou senha inválidos", exception.getMessage());

        // Assert (verificacao)
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(5, savedUser.getFailedAttempts(), "As tentativas falhas devem subir para 5");
        assertNotNull(savedUser.getBlockedUntil(), "O campo blockedUntil não pode ser nulo");

        assertTrue(savedUser.getBlockedUntil().isAfter(java.time.OffsetDateTime.now()), "O bloqueio deve ser no futuro");

    }

    @Test
    @DisplayName("Deve autenticar com sucesso e zerar o contador de tentativas falhas")
    void shouldResetFailedAttemptsOnSuccessfulLogin(){

        // Arrange
        String login = "marcelo.santos";
        String password = "senha_correta";
        String token = "dofhiosdfghuioweurf43r43r4t.token";

        User mockUser = new User();
        mockUser.setLogin(login);
        mockUser.setPasswordHash("senha_correta");
        mockUser.setFailedAttempts(4);
        mockUser.setBlockedUntil(null);
        mockUser.setRoles(Set.of());


        LoginRequest request = new LoginRequest(login, password);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPasswordHash())).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn(token);

        // Act
        AuthResponse response = authService.authenticate(request);

        // assert

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(0, savedUser.getFailedAttempts(), "As tentativas falhas devem zerar");
        assertNull(savedUser.getBlockedUntil(), "O bloqueio deve ser nulo");

        assertNotNull(response, "A resposta não pode ser nula");
        assertEquals(token, response.accessToken(), "O token devolvido deve ser o mesmo gerado pelo TokenService");
        assertEquals("Bearer", response.tokenType(), "O tipo do token deve ser Bearer");
        assertEquals(login, response.user().login(), "O login do usuário no payload deve bater");
    }

    @Test
    @DisplayName("Deve negar o acesso e lançar exceção quando o usuário estiver inativo ou deletado")
    void shouldDenyAccessWhenUserIsInactive(){

        // Arrange
        String login = "marcelo.santos";
        String password = "senha_correta";

        User mockUser = new User();
        mockUser.setLogin(login);
        mockUser.setPasswordHash("senha_correta");
        mockUser.setStatus(0);

        LoginRequest request = new LoginRequest(login, password);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(request);
        });

        // assert
        verify(tokenService, never()).generateToken(any());

        assertEquals("Usuário inativo ou excluído do sistema.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve negar o acesso e lançar exceção enquanto o usuário estiver bloqueado")
    void shouldDenyAccessWhenUserIsBlocked() {
        // Arrange
        String login = "marcelo.santos";
        String password = "senha_correta";

        User mockUser = new User();
        mockUser.setLogin(login);
        mockUser.setPasswordHash("senha_correta");
        mockUser.setBlockedUntil(OffsetDateTime.now().plusMinutes(30));

        LoginRequest request = new LoginRequest(login, password);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(request);
        });

        // assert
        verify(tokenService, never()).generateToken(any());

        assertEquals("Usuário temporariamente bloqueado por excesso de tentativas.", exception.getMessage());
    }
}




























