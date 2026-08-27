package br.com.csm.service;

import br.com.csm.dto.ApplicationDTO;
import br.com.csm.dto.ApplicationDTO;
import br.com.csm.model.Application;
import br.com.csm.model.Role;
import br.com.csm.model.User;
import br.com.csm.repository.ApplicationRepository;
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
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ApplicationDTO.CreateResponse createApplication(ApplicationDTO.CreateRequest request) {


        if (applicationRepository.findByAcronym(request.acronym()).isPresent()) {
            throw new IllegalArgumentException("Já existe um sistema com o este acronimo.");
        }

        String clientId = "csm-" + request.acronym().toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String secret = UUID.randomUUID() + "-" + UUID.randomUUID();

        Application application = Application.builder()
                .clientId(clientId)
                .clientSecretHash(passwordEncoder.encode(secret))
                .name(request.name())
                .acronym(request.acronym())
                .url(request.url())
                .redirectUri(request.redirectUri())
                .status(1)
                .isPublished(false)
                .objective(request.objective())
                .notes(request.notes())
                .requester(request.requester())
                .projectStartDate(request.projectStartDate())
                .gitNamespace(request.gitNamespace())
                .build();

        Application app = applicationRepository.save(application);

        return ApplicationDTO.CreateResponse.builder()
                .id(app.getId())
                .name(app.getName())
                .acronym(app.getAcronym())
                .clientId(app.getClientId())
                .clientSecret(secret)
                .build();
    }

    public List<ApplicationDTO.ApplicationList> listAllApplications() {
        return applicationRepository.findAllByDeactivatedAtIsNull().stream()
                .map(app -> new ApplicationDTO.ApplicationList(
                        app.getId(),
                        app.getName(),
                        app.getAcronym(),
                        app.getUrl(),
                        app.getStatus(),
                        app.getUpdatedAt(),
                        app.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateApplication (UUID appId, ApplicationDTO.ApplicationUpdate request) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Sistema não encontrado!"));

        if (request.name() != null && !request.name().isBlank()) app.setName(request.name());
        if (request.acronym() != null && !request.acronym().isBlank()) app.setAcronym(request.acronym());
        if (request.url() != null && !request.url().isBlank()) app.setUrl(request.url());
        if (request.status() != null) app.setStatus(request.status());

        app.setUpdatedAt(OffsetDateTime.now());
        applicationRepository.save(app);
    }

    @Transactional
    public void deleteApplication(UUID appId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Sistema não encontrado!"));

        // Soft Delete
        app.setDeactivatedAt(OffsetDateTime.now());
        app.setStatus(0); // 0 = Inativo

        applicationRepository.save(app);
    }
}
