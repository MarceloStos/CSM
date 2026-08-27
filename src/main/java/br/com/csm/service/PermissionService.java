package br.com.csm.service;

import br.com.csm.dto.PermissionDTO;
import br.com.csm.dto.RoleDTO;
import br.com.csm.dto.UserDTO;
import br.com.csm.model.Application;
import br.com.csm.model.Permission;
import br.com.csm.model.Role;
import br.com.csm.model.User;
import br.com.csm.repository.ApplicationRepository;
import br.com.csm.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public PermissionDTO.Response createPermission (PermissionDTO.CreateRequest request) {

        Application app = applicationRepository.findById(request.applicationId())
                .orElseThrow(() -> new IllegalArgumentException("Aplicação não encontrada."));

        if (permissionRepository.existsByApplicationIdAndName(app.getId(), request.name())){
            throw new IllegalArgumentException("Já existe uma permissão com este nome para essa aplicação");
        }

        Permission permission = Permission.builder()
                .name(request.name())
                .description(request.description())
                .application(app)
                .build();

        Permission savedPermission = permissionRepository.save(permission);

        return PermissionDTO.Response.builder()
                .id(savedPermission.getId())
                .name(savedPermission.getName())
                .description(savedPermission.getDescription())
                .applicationId(app.getId()).build();

    }

    public List<PermissionDTO.PermissionList> listAllPermissions () {
        return permissionRepository.findAll().stream()
                .map(permission -> new PermissionDTO.PermissionList(
                        permission.getId(),
                        permission.getName(),
                        permission.getDescription(),
                        permission.getApplication().getName(),
                        permission.getApplication().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePermission (UUID permissionId, PermissionDTO.PermissionUpdate request) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permissão não encontrada!"));

        if (request.name() != null && !request.name().isBlank()) permission.setName(request.name());
        if (request.description() != null && !request.description().isBlank()) permission.setDescription(request.description());

        permissionRepository.save(permission);
}

    @Transactional
    public void deletePermission(UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permissão não encontrada!"));

        // Ver como deletar uma permissao (analisar se deve excluir uma permissao)
    }
}
