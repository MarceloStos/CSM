package br.com.csm.service;

import br.com.csm.dto.application.CreateRequestDTO;
import br.com.csm.dto.application.CreateResponseDTO;
import br.com.csm.model.Application;
import br.com.csm.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public CreateResponseDTO create (CreateRequestDTO request) {
        String clientId = "csm-" + request.acronym().toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String secret = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        Application application = Application.builder()
                .clientId(clientId)
                .clientSecretHash(secret)
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

        Application newApp = applicationRepository.save(application);

        return CreateResponseDTO.builder()
                .id(newApp.getId())
                .name(newApp.getName())
                .acronym(newApp.getAcronym())
                .clientId(newApp.getClientId())
                .clientSecret(newApp.getClientSecretHash())
                .build();
    }
}
