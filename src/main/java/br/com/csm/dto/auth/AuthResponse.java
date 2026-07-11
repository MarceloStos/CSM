package br.com.csm.dto.auth;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
    private UserSummary user;

    @Data
    @Builder
    public static class UserSummary {
        private UUID id;
        private String name;
        private String login;
    }
}
