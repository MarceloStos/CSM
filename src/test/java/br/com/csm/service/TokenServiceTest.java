package br.com.csm.service;

import br.com.csm.core.security.TokenService;
import br.com.csm.model.Permission;
import br.com.csm.model.Role;
import br.com.csm.user.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    private TokenService tokenService;

    private final String jwtSecret = "minha-chave-secreta-para-testes-12345";

    @BeforeEach
    void setUp() {
        // Inicializa o serviço e injeta o valor da secret simulando o @Value
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", jwtSecret);
    }

    @Test
    @DisplayName("Deve gerar token com permissões mescladas e sem duplicidade")
    void shouldGenerateTokenWithDeduplicatedPermissions() {
        // 1. ARRANGE
        Permission permView = new Permission(); permView.setName("USER_VIEW"); permView.setId(UUID.randomUUID());
        Permission permCreate = new Permission(); permCreate.setName("USER_CREATE"); permCreate.setId(UUID.randomUUID());
        Permission permManage = new Permission(); permManage.setName("SYSTEM_MANAGE"); permManage.setId(UUID.randomUUID());

        Role admin = new Role();
        admin.setName("ADMIN");
        admin.setId(UUID.randomUUID());
        admin.setPermissions(Set.of(permView, permCreate, permManage));

        Role manager = new Role();
        manager.setName("MANAGER");
        manager.setId(UUID.randomUUID());
        // O Manager TAMBEM tem USER_VIEW (Esta é a duplicidade proposital)
        manager.setPermissions(Set.of(permView));

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin("marcelo.santos");
        user.setName("Marcelo");
        user.setRoles(Set.of(admin, manager));

        // 2. ACT
        String token = tokenService.generateToken(user);
        DecodedJWT decodedJWT = tokenService.validateTokenAndGetClaims(token);

        // 3. ASSERT
        assertNotNull(token);
        assertNotNull(decodedJWT);

        // Extrai a lista de claims de dentro do payload do JWT
        List<String> tokenPermissions = decodedJWT.getClaim("permissions").asList(String.class);

        assertEquals(3, tokenPermissions.size(), "O token deve conter apenas 3 permissões (o USER_VIEW repetido foi removido)");
        assertTrue(tokenPermissions.contains("USER_VIEW"));
        assertTrue(tokenPermissions.contains("USER_CREATE"));
        assertTrue(tokenPermissions.contains("SYSTEM_MANAGE"));
    }

    @Test
    @DisplayName("Deve retornar null ao validar um token expirado")
    void shouldReturnNullWhenTokenIsExpired() {
        // 1. ARRANGE

        // Como o metodo generateToken() obriga o token a valer 15 minutos pro futuro,
        // crio o token manualmente aqui, forçando a data para o passado.
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        String expiredToken = JWT.create()
                .withIssuer("csm-iam-api")
                .withSubject("marcelo.santos")
                .withExpiresAt(Instant.now().minus(1, ChronoUnit.HOURS)) // Expirou há 1 hora
                .sign(algorithm);

        // 2. ACT
        DecodedJWT decodedJWT = tokenService.validateTokenAndGetClaims(expiredToken);

        // 3. ASSERT
        assertNull(decodedJWT, "O token expirado deve ser rejeitado e retornar null");
    }

    @Test
    @DisplayName("Deve retornar null ao validar um token assinado com secret falsa")
    void shouldReturnNullWhenTokenHasInvalidSignature() {
        // 1. ARRANGE
        // Um hacker tentou gerar um token na máquina dele usando uma senha inventada
        String hackerSecret = "senha-inventada-pelo-hacker";
        Algorithm hackerAlgorithm = Algorithm.HMAC256(hackerSecret);

        String fakeToken = JWT.create()
                .withIssuer("csm-iam-api")
                .withSubject("hacker")
                .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS)) // Validade está correta
                .sign(hackerAlgorithm); // Assinado com a senha errada

        // 2. ACT
        // O TokenService vai tentar abrir usando a SECRET oficial
        DecodedJWT decodedJWT = tokenService.validateTokenAndGetClaims(fakeToken);

        // 3. ASSERT
        assertNull(decodedJWT, "O token com assinatura falsa deve ser barrado e retornar null");
    }
}





















