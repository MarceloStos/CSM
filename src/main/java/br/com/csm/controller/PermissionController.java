package br.com.csm.controller;

import br.com.csm.dto.PermissionDTO;
import br.com.csm.dto.RoleDTO;
import br.com.csm.model.Permission;
import br.com.csm.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ResponseEntity<PermissionDTO.Response> createPermission (@Valid @RequestBody PermissionDTO.CreateRequest request) {
        PermissionDTO.Response response = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PermissionDTO.PermissionList>> listAllPermissions() {
        List<PermissionDTO.PermissionList> permissions = permissionService.listAllPermissions();
        return ResponseEntity.status(HttpStatus.OK).body(permissions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePermisson(@PathVariable UUID id, @RequestBody PermissionDTO.PermissionUpdate request) {
        permissionService.updatePermission(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Sucesso, sem corpo de resposta
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }

}
