package br.com.csm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserDTO {

    @Builder
    public record CreateRequest (
            @NotBlank(message = "O nome é obrigatótrio")
            String name,
            @NotBlank(message = "O CPF é obrigatório")
            String cpf,
            @NotBlank(message = "O login é obrigatótrio")
            String login,
            @NotBlank
            @Email(message = "Formato de e-mail inválido")
            String email,
            @NotBlank(message = "A senha é obrigatótrio")
            @Size(min = 8, message = "A senha deve ter no minimo 8 caracteres")
            String password,
            Set<UUID> roleIds
    ){
    }

    @Builder
    public record UserResponse(
            UUID id,
            String name,
            String cpf,
            String login,
            String email,
            Integer status,
            OffsetDateTime createdAt
    ) {}

    @Builder
    public record UserInfoRequest(
            @NotBlank(message = "O ID do usuário é obrigatório")
            UUID id
    ){
    }

    @Builder
    public record UserInfoResponse (

            UUID id,
            String name,
            String cpf,
            String login,
            String email,
            UserDate userDate,
            UserCorporativeData userCorporativeData,
            UserInfoResponse.UserSecurity userSecurity
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

    @Builder
    public record UserList(
            UUID id,
            String name,
            String login,
            String email,
            Integer status,
            List<UUID> roleIds
    ){}

    @Builder
    public record UserUpdate(
            String name,
            String login,
            String email,
            String password,
            Set<UUID> roleIds
    ){}

}
