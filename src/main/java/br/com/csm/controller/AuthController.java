package br.com.csm.controller;

import br.com.csm.dto.AuthDTO;
import br.com.csm.dto.PermissionDTO;
import br.com.csm.dto.UserDTO;
import br.com.csm.model.User;
import br.com.csm.service.AuthService;
import br.com.csm.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<AuthDTO.AuthResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        AuthDTO.AuthResponse response = authService.authenticate(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/userinfo")
    public ResponseEntity<UserDTO.UserInfoResponse> userInfo(@Valid @RequestBody UserDTO.UserInfoRequest request) {

        User user = authService.userInfo(request.id());

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
