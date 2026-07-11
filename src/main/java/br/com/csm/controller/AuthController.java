package br.com.csm.controller;

import br.com.csm.dto.auth.AuthResponse;
import br.com.csm.dto.auth.LoginRequest;
import br.com.csm.dto.user.UserInfoRequest;
import br.com.csm.dto.user.UserInfoResponse;
import br.com.csm.model.User;
import br.com.csm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        User authenticatedUser = authService.authenticate(request.getLogin(), request.getPassword());

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...mock_token_temporario";

        AuthResponse.UserSummary userSummary = AuthResponse.UserSummary.builder()
                .id(authenticatedUser.getId())
                .name(authenticatedUser.getName())
                .login(authenticatedUser.getLogin())
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(900)
                .user(userSummary)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userinfo")
    public ResponseEntity<UserInfoResponse> UserInfo(@Valid @RequestBody UserInfoRequest request) {

        User user = authService.userInfo(request.getId());

        UserInfoResponse.UserDate userDate = UserInfoResponse.UserDate.builder()
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .lastLogin(user.getLastLogin())
                .build();

        UserInfoResponse.UserCorporativeData userCorporativeData = UserInfoResponse.UserCorporativeData.builder()
                .objectguid(user.getObjectguid())
                .registrationNumber(user.getRegistrationNumber())
                .status(user.getStatus())
                .hiddenTutorial(user.getHiddenTutorial())
                .unitId(user.getUnitId())
                .contractId(user.getContractId())
                .photoId(user.getPhotoId())
                .build();

        UserInfoResponse.UserSecurity userSecurity = UserInfoResponse.UserSecurity.builder()
                .failedAttempts(user.getFailedAttempts())
                .blockedUntil(user.getBlockedUntil())
                .build();

        UserInfoResponse response = UserInfoResponse.builder()
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
