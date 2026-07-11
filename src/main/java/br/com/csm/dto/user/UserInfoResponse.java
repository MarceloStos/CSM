package br.com.csm.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class UserInfoResponse {

    private UUID id;
    private String name;
    private String cpf;
    private String login;
    private String email;
    private UserDate userDate;
    private UserCorporativeData userCorporativeData;
    private UserSecurity userSecurity;

    @Data
    @Builder
    public static class UserSecurity {
        private Integer failedAttempts;
        private OffsetDateTime blockedUntil;
    }

    @Data
    @Builder
    public static class UserCorporativeData {
        private String objectguid;
        private Integer registrationNumber;
        private Integer status;
        private Boolean hiddenTutorial;
        private Integer unitId;
        private Integer contractId;
        private Integer photoId;
    }

    @Data
    @Builder
    public static class UserDate {
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private OffsetDateTime deletedAt;
        private OffsetDateTime lastLogin;
    }
}