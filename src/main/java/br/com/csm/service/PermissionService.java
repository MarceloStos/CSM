package br.com.csm.service;

import br.com.csm.dto.PermissionDTO;
import br.com.csm.dto.RoleDTO;
import br.com.csm.model.Application;
import br.com.csm.model.Permission;
import br.com.csm.repository.ApplicationRepository;
import br.com.csm.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new IllegalArgumentException("Já existe um perfil com este nome para essa aplicação");
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
}
