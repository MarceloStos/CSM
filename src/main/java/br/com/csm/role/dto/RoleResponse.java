package br.com.csm.role.dto;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record RoleResponse(
        UUID id,
        String name,
        String description,
        Integer status,
        UUID applicationId,
        Set<String> permissions
){}