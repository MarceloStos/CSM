package br.com.csm.service;

import br.com.csm.dto.RoleDTO;
import br.com.csm.model.Application;
import br.com.csm.model.Permission;
import br.com.csm.model.Role;
import br.com.csm.repository.ApplicationRepository;
import br.com.csm.repository.PermissionRepository;
import br.com.csm.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public RoleDTO.Response createRole (RoleDTO.CreateRequest request) {
        Application app = applicationRepository.findById(request.applicationId())
                .orElseThrow(() -> new IllegalArgumentException("Aplicação não encontrada."));

        if (roleRepository.existsByApplicationIdAndName(app.getId(), request.name())) {
            throw new IllegalArgumentException("Já existe um perfil com este nome para essa aplicação");
        }

        Set<Permission> permissions = new HashSet<>();
        if (request.permissionIds() != null && !request.permissionIds().isEmpty()) {
            List<Permission> permissionList = permissionRepository.findAllById(request.permissionIds());

            // Verificar se um hacker não tentou injetar uma permissão do RH num perfil do Estoque
            boolean allMatchApp = permissionList.stream()
                    .allMatch(p -> p.getApplication().getId().equals(app.getId()));

            if (!allMatchApp) {
                throw new IllegalArgumentException("Atenção: Uma ou mais permissões informadas não pertencem a esta aplicação.");
            }
            permissions.addAll(permissionList);
        }

        Role role = Role.builder()
                .name(request.name())
                .description(request.description())
                .application(app)
                .permissions(permissions)
                .status(1)
                .build();

        Role savedrole = roleRepository.save(role);

        return RoleDTO.Response.builder()
                .id(savedrole.getId())
                .name(savedrole.getName())
                .description(savedrole.getDescription())
                .applicationId(app.getId())
                .build();
    }

    public List<RoleDTO.RoleList> listAllRoles () {
        return roleRepository.findByStatusNot(0).stream()
                .map(role -> new RoleDTO.RoleList(
                        role.getId(),
                        role.getName(),
                        role.getDescription(),
                        role.getApplication().getName(),
                        role.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateRole (UUID roleId, RoleDTO.RoleUpdate request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado!"));

        if (request.name() != null && !request.name().isBlank()) role.setName(request.name());
        if (request.description() != null && !request.description().isBlank()) role.setDescription(request.description());
        if (request.status() != null) role.setStatus(request.status());

        if (request.permissionsIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.permissionsIds());
            boolean allMatchApp = permissions.stream()
                    .allMatch(p -> p.getApplication().getId().equals(role.getApplication().getId()));

            if (!allMatchApp) {
                throw new IllegalArgumentException("Uma ou mais permissões não pertencem à aplicação deste perfil.");
            }

            role.setPermissions(new HashSet<>(permissions));
        }

        role.setUpdatedAt(OffsetDateTime.now());
        roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado!"));

        // Soft Delete
        role.setStatus(0); // 0 = Inativo

        roleRepository.save(role);
    }

}
