package br.com.csm.service;

import br.com.csm.dto.UserDTO;
import br.com.csm.model.Role;
import br.com.csm.model.User;
import br.com.csm.repository.RoleRepository;
import br.com.csm.repository.UserRepository;
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
    public UserDTO.UserResponse createUser(UserDTO.CreateRequest request) {

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

    public UserDTO.UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        return responseDTO(user);
    }

    public List<UserDTO.UserList> listAllUsers () {
        return userRepository.findAllByDeletedAtIsNull().stream()
                .map(user -> new UserDTO.UserList(
                        user.getId(),
                        user.getName(),
                        user.getLogin(),
                        user.getEmail(),
                        user.getStatus(),
                        user.getRoles().stream().map(Role::getId).toList()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUser (UUID userId, UserDTO.UserUpdate request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        if (request.name() != null && !request.name().isBlank()) user.setName(request.name());
        if (request.login() != null && !request.login().isBlank()) user.setLogin(request.login());
        if (request.email() != null && !request.email().isBlank()) user.setEmail(request.email());

        // Só atualiza a senha se ela vier preenchida do frontend
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        if (request.roleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
            user.setRoles(roles);
        }

        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // Soft Delete
        user.setDeletedAt(OffsetDateTime.now());
        user.setStatus(0); // 0 = Inativo

        userRepository.save(user);
    }
    private UserDTO.UserResponse responseDTO(User user) {
        return UserDTO.UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .cpf(user.getCpf())
                .login(user.getLogin())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}