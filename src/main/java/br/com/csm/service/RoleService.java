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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
