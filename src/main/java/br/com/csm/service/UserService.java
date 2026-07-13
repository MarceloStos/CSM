package br.com.csm.service;

import br.com.csm.dto.user.CreateRequestDTO;
import br.com.csm.dto.user.UserResponseDTO;
import br.com.csm.model.User;
import br.com.csm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO createUser(CreateRequestDTO request) {

        // Para verificação de unicidade
        if (userRepository.findByLogin(request.login()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com o este login.");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este email.");
        }

        String password = request.password();

        User newUser = User.builder()
                .name(request.name())
                .cpf(request.cpf())
                .login(request.login())
                .email(request.email())
                .passwordHash(password)
                .status(1)
                .failedAttempts(0)
                .forcePasswordChange(true) // Força o usuário a trocar a senha no primeiro login
                .hiddenTutorial(false)
                .build();

        User savedUser = userRepository.save(newUser);

        return responseDTO(savedUser);
    }

    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        return responseDTO(user);
    }

    private UserResponseDTO responseDTO(User user) {
        return UserResponseDTO.builder()
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