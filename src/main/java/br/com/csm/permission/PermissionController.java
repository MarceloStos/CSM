package br.com.csm.permission;


import br.com.csm.permission.dto.PermissionCreateRequest;
import br.com.csm.permission.dto.PermissionResponse;
import br.com.csm.permission.dto.PermissionUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
//    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ResponseEntity<PermissionResponse> createPermission (@Valid @RequestBody PermissionCreateRequest request) {
        PermissionResponse response = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> listAllPermissions() {
        List<PermissionResponse> permissions = permissionService.listAllPermissions();
        return ResponseEntity.status(HttpStatus.OK).body(permissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> viewPermission(@PathVariable UUID id) {
        PermissionResponse permission = permissionService.getPermissionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(permission);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePermisson(@PathVariable UUID id, @RequestBody PermissionUpdateRequest request) {
        permissionService.updatePermission(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Sucesso, sem corpo de resposta
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }

}
