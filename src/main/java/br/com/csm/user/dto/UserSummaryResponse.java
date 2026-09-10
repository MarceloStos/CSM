package br.com.csm.user.dto;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserSummaryResponse(
        UUID id,
        String name,
        String login,
        String email,
        Integer status,
        Set<String> roles
){}