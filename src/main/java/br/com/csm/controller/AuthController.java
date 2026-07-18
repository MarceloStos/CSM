package br.com.csm.controller;

import br.com.csm.dto.auth.AuthResponseDTO;
import br.com.csm.dto.auth.LoginRequestDTO;
import br.com.csm.dto.user.UserInfoRequestDTO;
import br.com.csm.dto.user.UserInfoResponseDTO;
import br.com.csm.model.User;
import br.com.csm.service.AuthService;
import br.com.csm.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        User authenticatedUser = authService.authenticate(request.login(), request.password());

        String jwtToken = tokenService.generateToken(authenticatedUser);

        AuthResponseDTO.UserSummary userSummary = AuthResponseDTO.UserSummary.builder()
                .id(authenticatedUser.getId())
                .name(authenticatedUser.getName())
                .login(authenticatedUser.getLogin())
                .build();

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .expiresIn(900)
                .user(userSummary)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userinfo")
    public ResponseEntity<UserInfoResponseDTO> userInfo(@Valid @RequestBody UserInfoRequestDTO request) {

        User user = authService.userInfo(request.id());

        UserInfoResponseDTO.UserDate userDate = UserInfoResponseDTO.UserDate.builder()
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .lastLogin(user.getLastLogin())
                .build();

        UserInfoResponseDTO.UserCorporativeData userCorporativeData = UserInfoResponseDTO.UserCorporativeData.builder()
                .objectguid(user.getObjectguid())
                .registrationNumber(user.getRegistrationNumber())
                .status(user.getStatus())
                .hiddenTutorial(user.getHiddenTutorial())
                .unitId(user.getUnitId())
                .contractId(user.getContractId())
                .photoId(user.getPhotoId())
                .build();

        UserInfoResponseDTO.UserSecurity userSecurity = UserInfoResponseDTO.UserSecurity.builder()
                .failedAttempts(user.getFailedAttempts())
                .blockedUntil(user.getBlockedUntil())
                .build();

        UserInfoResponseDTO response = UserInfoResponseDTO.builder()
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
