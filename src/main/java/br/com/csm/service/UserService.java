package br.com.csm.service;

import br.com.csm.dto.UserDTO;
import br.com.csm.model.User;
import br.com.csm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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
                .build();

        User savedUser = userRepository.save(newUser);

        return responseDTO(savedUser);
    }

    public UserDTO.UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        return responseDTO(user);
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