package br.com.csm.user;

import br.com.csm.role.Role;
import br.com.csm.role.RoleRepository;
import br.com.csm.user.dto.UserCreateRequest;
import br.com.csm.user.dto.UserDetailsResponse;
import br.com.csm.user.dto.UserSummaryResponse;
import br.com.csm.user.dto.UserUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDetailsResponse createUser(UserCreateRequest request) {

        // Para verificação de unicidade
        if (userRepository.findByLogin(request.login()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com o este login.");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este email.");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        Set<Role> userRoles = new HashSet<>();
        if (request.roleIds() != null && !request.roleIds().isEmpty()) {
            userRoles.addAll(roleRepository.findAllById(request.roleIds()));
        }

        User newUser = User.builder()
                .name(request.name())
                .cpf(request.cpf())
                .login(request.login())
                .email(request.email())
                .passwordHash(hashedPassword)
                .status(1)
                .failedAttempts(0)
                .forcePasswordChange(true) // Força o usuário a trocar a senha no primeiro login
                .hiddenTutorial(false)
                .roles(userRoles)
                .build();

        User savedUser = userRepository.save(newUser);

        return responseDTO(savedUser);
    }

    public UserDetailsResponse getUserById(UUID userId) {
        User user = findEntityById(userId);

        return responseDTO(user);
    }

    public List<UserSummaryResponse> listAllUsers () {
        return userRepository.findAllByDeletedAtIsNull().stream()
                .map(user -> new UserSummaryResponse(
                        user.getId(),
                        user.getName(),
                        user.getLogin(),
                        user.getEmail(),
                        user.getStatus(),
                        user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                ))
                .toList();
    }

    @Transactional
    public void updateUser (UUID userId, UserUpdateRequest request) {
        User user = findEntityById(userId);

        if (request.name() != null && !request.name().isBlank()) user.setName(request.name());
        if (request.email() != null && !request.email().isBlank()) user.setEmail(request.email());

        if (request.status() != null) user.setStatus(request.status());

        if (request.roleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
            user.setRoles(roles);
        }

        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = findEntityById(userId);

        // Soft Delete
        user.setDeletedAt(OffsetDateTime.now());
        user.setStatus(0); // 0 = Inativo

        userRepository.save(user);
    }
    private UserDetailsResponse responseDTO(User user) {
        return UserDetailsResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .cpf(user.getCpf())
                .login(user.getLogin())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .userDate(UserDetailsResponse.UserDate.builder()
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .deletedAt(user.getDeletedAt())
                        .lastLogin(user.getLastLogin())
                        .build())
                .userCorporativeData(UserDetailsResponse.UserCorporativeData.builder()
                        .objectguid(user.getObjectguid())
                        .registrationNumber(user.getRegistrationNumber())
                        .status(user.getStatus())
                        .hiddenTutorial(false)
                        .unitId(user.getUnitId())
                        .contractId(user.getContractId()).photoId(user.getPhotoId())
                        .build())
                .userSecurity(UserDetailsResponse.UserSecurity.builder()
                        .failedAttempts(user.getFailedAttempts())
                        .blockedUntil(user.getBlockedUntil())
                        .build())
                .build();
    }

    private User findEntityById (UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado!"));
    }
}