package br.com.csm.user.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserDetailsResponse(

        UUID id,
        String name,
        String cpf,
        String login,
        String email,
        Set<String> roles,
        UserDate userDate,
        UserCorporativeData userCorporativeData,
        UserSecurity userSecurity
) {
    @Builder
    public record UserSecurity (
            Integer failedAttempts,
            OffsetDateTime blockedUntil
    ){}

    @Builder
    public record UserCorporativeData (
            String objectguid,
            Integer registrationNumber,
            Integer status,
            Boolean hiddenTutorial,
            Integer unitId,
            Integer contractId,
            Integer photoId
    ){}

    @Builder
    public record UserDate (
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime deletedAt,
            OffsetDateTime lastLogin
    ){}
}