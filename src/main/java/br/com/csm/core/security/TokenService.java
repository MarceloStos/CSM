package br.com.csm.core.security;

import br.com.csm.permission.Permission;
import br.com.csm.role.Role;
import br.com.csm.user.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TokenService {

    @Value("${CSM_JWT_SECRET}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .toList();

            List<String> permissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .distinct()
                    .toList();

            return JWT.create()
                    .withIssuer("csm-iam-api")
                    .withSubject(user.getLogin())
                    .withClaim("id", user.getId().toString())
                    .withClaim("name", user.getName())
                    .withClaim("permissions", permissions)
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Ocorreu um erro ao gerar o token JWT", exception);
        }
    }

    public DecodedJWT validateTokenAndGetClaims(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("csm-iam-api")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            // Token adulterado, expirado ou não esta assinado
            return null;
        }
    }

    private Instant generateExpirationDate() {
        return OffsetDateTime.now().plusMinutes(10).toInstant();
    }

}
