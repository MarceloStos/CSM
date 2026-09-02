package br.com.csm.controller;

import br.com.csm.dto.AuthDTO;
import br.com.csm.dto.UserDTO;
import br.com.csm.model.User;
import br.com.csm.service.AuthService;
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

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        AuthDTO.AuthResponse response = authService.authenticate(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserDTO.UserInfoResponse> userInfo() {

        // 1. Pega o usuário raso (apenas com ID e Login) do contexto de segurança do Spring
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User tokenUser = (User) authentication.getPrincipal();

        // 2. Busca o usuário com todos os dados corporativos no banco usando o ID seguro do token
        User user = authService.userInfo(tokenUser.getId());

        // 3. Constrói as partes da resposta
        UserDTO.UserInfoResponse.UserDate userDate = UserDTO.UserInfoResponse.UserDate.builder()
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .lastLogin(user.getLastLogin())
                .build();

        UserDTO.UserInfoResponse.UserCorporativeData userCorporativeData = UserDTO.UserInfoResponse.UserCorporativeData.builder()
                .objectguid(user.getObjectguid())
                .registrationNumber(user.getRegistrationNumber())
                .status(user.getStatus())
                .hiddenTutorial(user.getHiddenTutorial())
                .unitId(user.getUnitId())
                .contractId(user.getContractId())
                .photoId(user.getPhotoId())
                .build();

        UserDTO.UserInfoResponse.UserSecurity userSecurity = UserDTO.UserInfoResponse.UserSecurity.builder()
                .failedAttempts(user.getFailedAttempts())
                .blockedUntil(user.getBlockedUntil())
                .build();

        // 4. Constrói o response final
        UserDTO.UserInfoResponse response = UserDTO.UserInfoResponse.builder()
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
}