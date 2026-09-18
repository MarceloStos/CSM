package br.com.csm.auth;

import br.com.csm.auth.dto.AuthResponse;
import br.com.csm.auth.dto.LoginRequest;
import br.com.csm.auth.dto.TokenRefreshRequest;
import br.com.csm.auth.dto.TokenRefreshResponse;
import br.com.csm.user.User;
import br.com.csm.user.dto.UserDetailsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserDetailsResponse> userInfo() {

        // 1. Pega o usuário raso (apenas com ID e Login) do contexto de segurança do Spring
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User tokenUser = (User) authentication.getPrincipal();

        // 2. Busca o usuário com todos os dados corporativos no banco usando o ID seguro do token
        User user = authService.userInfo(tokenUser.getId());

        // 3. Constrói as partes da resposta
        UserDetailsResponse.UserDate userDate = UserDetailsResponse.UserDate.builder()
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .lastLogin(user.getLastLogin())
                .build();

        UserDetailsResponse.UserCorporativeData userCorporativeData = UserDetailsResponse.UserCorporativeData.builder()
                .objectguid(user.getObjectguid())
                .registrationNumber(user.getRegistrationNumber())
                .status(user.getStatus())
                .hiddenTutorial(user.getHiddenTutorial())
                .unitId(user.getUnitId())
                .contractId(user.getContractId())
                .photoId(user.getPhotoId())
                .build();

        UserDetailsResponse.UserSecurity userSecurity = UserDetailsResponse.UserSecurity.builder()
                .failedAttempts(user.getFailedAttempts())
                .blockedUntil(user.getBlockedUntil())
                .build();

        // 4. Constrói o response final
        UserDetailsResponse response = UserDetailsResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .cpf(user.getCpf())
                .login(user.getLogin())
                .email(user.getEmail())
                .userDate(userDate)
                .userCorporativeData(userCorporativeData)
                .userSecurity(userSecurity)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken (@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}