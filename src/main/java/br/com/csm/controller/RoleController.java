package br.com.csm.controller;

import br.com.csm.dto.RoleDTO;
import br.com.csm.dto.UserDTO;
import br.com.csm.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleDTO.Response> createRole (@Valid @RequestBody RoleDTO.CreateRequest request) {
        RoleDTO.Response response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<RoleDTO.RoleList>> listAll() {
        List<RoleDTO.RoleList> roles = roleService.listAllRoles();
        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRole(@PathVariable UUID id, @RequestBody RoleDTO.RoleUpdate request) {
        roleService.updateRole(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Sucesso, sem corpo de resposta
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }
}