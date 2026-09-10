package br.com.csm.role;

import br.com.csm.role.dto.RoleCreateRequest;
import br.com.csm.role.dto.RoleResponse;
import br.com.csm.role.dto.RoleUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
//    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleResponse> createRole (@Valid @RequestBody RoleCreateRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<RoleResponse>> listAllRoles() {
        List<RoleResponse> roles = roleService.listAllRoles();
        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRole(@PathVariable UUID id, @RequestBody RoleUpdateRequest request) {
        roleService.updateRole(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Sucesso, sem corpo de resposta
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }
}