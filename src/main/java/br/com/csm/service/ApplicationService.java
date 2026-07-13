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
        String clientId = "csm-" + request.getAcronym().toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String secret = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        Application application = Application.builder()
                .clientId(clientId)
                .clientSecretHash(secret)
                .name(request.getName())
                .acronym(request.getAcronym())
                .url(request.getUrl())
                .redirectUri(request.getRedirectUri())
                .status(1)
                .isPublished(false)
                .objective(request.getObjective())
                .notes(request.getNotes())
                .requester(request.getRequester())
                .projectStartDate(request.getProjectStartDate())
                .gitNamespace(request.getGitNamespace())
                .build();

        Application app = applicationRepository.save(application);

        return CreateResponseDTO.builder()
                .id(app.getId())
                .name(app.getName())
                .acronym(app.getAcronym())
                .clientId(app.getClientId())
                .clientSecret(app.getClientSecretHash())
                .build();
    }
}
