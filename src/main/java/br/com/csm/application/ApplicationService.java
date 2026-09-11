package br.com.csm.application;

import br.com.csm.application.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ApplicationCreateResponse createApplication(ApplicationCreateRequest request) {

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

        return ApplicationCreateResponse.builder()
                .id(app.getId())
                .name(app.getName())
                .acronym(app.getAcronym())
                .clientId(app.getClientId())
                .clientSecret(secret)
                .build();
    }

    public List<ApplicationSummaryResponse> listAllApplications() {
        return applicationRepository.findAllByDeactivatedAtIsNull().stream()
                .map(app -> new ApplicationSummaryResponse(
                        app.getId(),
                        app.getName(),
                        app.getAcronym(),
                        app.getUrl(),
                        app.getStatus(),
                        app.getIsPublished(),
                        app.getUpdatedAt(),
                        app.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public ApplicationDetailsResponse getApplicationById(UUID appId) {
        Application app = findEntityById(appId);
        return responseDTO(app);
    }

    @Transactional
    public void updateApplication (UUID appId, ApplicationUpdateRequest request) {
        Application app = findEntityById(appId);

        if (request.name() != null && !request.name().isBlank()) app.setName(request.name());
        if (request.acronym() != null && !request.acronym().isBlank()) app.setAcronym(request.acronym());
        if (request.url() != null && !request.url().isBlank()) app.setUrl(request.url());
        if (request.status() != null) app.setStatus(request.status());
        if (request.isPublished() != null) app.setIsPublished(request.isPublished());

        app.setUpdatedAt(OffsetDateTime.now());
        applicationRepository.save(app);
    }

    @Transactional
    public void deleteApplication(UUID appId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new EntityNotFoundException("Sistema não encontrado!"));

        // Soft Delete
        app.setDeactivatedAt(OffsetDateTime.now());
        app.setStatus(0); // 0 = Inativo

        applicationRepository.save(app);
    }

    private ApplicationDetailsResponse responseDTO(Application app) {
        return ApplicationDetailsResponse.builder()
                .id(app.getId())
                .clientId(app.getClientId())
                .name(app.getName())
                .acronym(app.getAcronym())
                .url(app.getUrl())
                .redirectUri(app.getRedirectUri())
                .status(app.getStatus())
                .isPublished(app.getIsPublished())
                .governanceData(ApplicationDetailsResponse.AppGovernanceData.builder()
                        .objective(app.getObjective())
                        .notes(app.getNotes())
                        .requester(app.getRequester())
                        .projectStartDate(app.getProjectStartDate())
                        .gitNamespace(app.getGitNamespace())
                        .build())
                .dateData(ApplicationDetailsResponse.AppDateData.builder()
                        .createdAt(app.getCreatedAt())
                        .updatedAt(app.getUpdatedAt())
                        .deactivatedAt(app.getDeactivatedAt())
                        .build())
                .build();
    }

    private Application findEntityById(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sistema não encontrado!"));
    }
}
