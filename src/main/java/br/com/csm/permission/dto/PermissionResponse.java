package br.com.csm.permission.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PermissionResponse(
        UUID id,
        String name,
        String description,
        UUID applicationId,
        String applicationName
) {}