package br.com.csm.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // 7 dias em segundos (7 * 24 * 60 * 60 = 604800)
    @Value("${jwt.refresh.expiration.seconds:604800}")
    private Long refreshTokenDurationSeconds;

    private final RefreshTokenRedisRepository redisRepository;

    public RefreshToken createRefreshToken(UUID userId) {
        // Deletar o token antigo se o usuário fizer um novo login
        redisRepository.deleteByUserId(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expirationInSeconds(refreshTokenDurationSeconds)
                .build();

        return redisRepository.save(refreshToken);
    }

    public RefreshToken verifyAndGetToken(String token) {
        // Se o findById retornar algo, é porque NÃO expirou.
        return redisRepository.findById(token)
                .orElseThrow(() -> new RuntimeException("Refresh token expirado ou inválido. Faça login novamente."));
    }
}