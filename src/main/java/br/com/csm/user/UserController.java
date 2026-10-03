package br.com.csm.user;

import br.com.csm.user.dto.UserCreateRequest;
import br.com.csm.user.dto.UserDetailsResponse;
import br.com.csm.user.dto.UserSummaryResponse;
import br.com.csm.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<List<UserSummaryResponse>> listAllUsers() {
        List<UserSummaryResponse> users = userService.listAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<UserDetailsResponse> viewUser(@PathVariable UUID id) {
        UserDetailsResponse user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserDetailsResponse> createUser (@Valid @RequestBody UserCreateRequest request) {
        UserDetailsResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Sucesso, sem corpo de resposta
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }
}
