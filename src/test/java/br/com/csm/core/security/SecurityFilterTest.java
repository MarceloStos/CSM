package br.com.csm.core.security;

import br.com.csm.user.User;
import br.com.csm.user.UserRepository;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    // Simulando os objetos da Web (Requisição e Resposta)
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    // A cada teste que rodar, nós limpamos o contexto de segurança para um não interferir no outro
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve ignorar o filtro e não preencher contexto se não houver token")
    void shouldIgnoreFilterWhenNoTokenIsProvided() throws ServletException, IOException {
        // 1. ARRANGE
        // Quando o filtro tentar ler o cabeçalho "Authorization", devolvemos nulo (Nenhum token foi enviado)
        when(request.getHeader("Authorization")).thenReturn(null);

        // 2. ACT
        // O metodo doFilter executa a logica e passa para o prxoimo filtro
        securityFilter.doFilter(request, response, filterChain);

        // 3. ASSERT
        // Garante que o filtro nao travou e repassou a requisicao para frente
        verify(filterChain, times(1)).doFilter(request, response);

        // Garante que o Spring Security CONTINUA VAZIO (o usuário não foi autenticado)
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "O contexto de segurança deve permanecer nulo");
    }

    @Test
    @DisplayName("Deve autenticar o usuário e preencher o contexto quando o token for válido")
    void shouldAuthenticateWhenTokenIsValid() throws ServletException, IOException {
        // 1. ARRANGE
        String token = "token.jwt.valido";
        String login = "marcelo.santos";
        String userId = UUID.randomUUID().toString();
        List<String> expectedPermissions = List.of("USER_VIEW", "USER_CREATE");

        // Simula o envio do Header pelo front-end
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Cria o JWT e as Claims (peças do payload) falsas
        DecodedJWT mockJwt = mock(DecodedJWT.class);
        Claim mockIdClaim = mock(Claim.class);
        Claim mockPermissionsClaim = mock(Claim.class);

        // Ensina as Claims a devolverem os tipos exatos que o seu filtro pede (.asString e .asList)
        when(mockIdClaim.asString()).thenReturn(userId);
        when(mockPermissionsClaim.asList(String.class)).thenReturn(expectedPermissions);

        // Ensina o JWT a entregar as Claims corretas
        when(mockJwt.getSubject()).thenReturn(login);
        when(mockJwt.getClaim("id")).thenReturn(mockIdClaim);
        when(mockJwt.getClaim("permissions")).thenReturn(mockPermissionsClaim);

        // Quando o tokenService validar, devolve o nosso JWT falso já preparado
        when(tokenService.validateTokenAndGetClaims(token)).thenReturn(mockJwt);

        // 2. ACT
        securityFilter.doFilterInternal(request, response, filterChain);

        // 3. ASSERT
        // Garante que o request continuou o fluxo normal para os Controllers
        verify(filterChain, times(1)).doFilter(request, response);

        // Valida se o Spring Security guardou o usuário no cofre
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "O contexto de segurança foi preenchido!");

        // Como o seu filtro salva o próprio model User no Contexto, podemos extraí-lo:
        User authenticatedUser = (User) auth.getPrincipal();
        assertEquals(login, authenticatedUser.getLogin(), "O login deve bater");
        assertEquals(UUID.fromString(userId), authenticatedUser.getId(), "O ID deve bater");

        // Valida se as permissões foram convertidas em Authorities com sucesso
        boolean hasUserView = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER_VIEW"));

        assertTrue(hasUserView, "A permissão USER_VIEW deve estar presente nas Authorities do Spring");
    }
}