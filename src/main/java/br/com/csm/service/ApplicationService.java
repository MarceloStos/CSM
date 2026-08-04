package br.com.csm.service;

import br.com.csm.dto.ApplicationDTO;
import br.com.csm.model.Application;
import br.com.csm.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationDTO.CreateResponse create (ApplicationDTO.CreateRequest request) {
        String clientId = "csm-" + request.acronym().toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String secret = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        Application application = Application.builder()
                .clientId(clientId)
                .clientSecretHash(secret)
                .name(request.gitNamespace())
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
                .clientSecret(app.getClientSecretHash())
                .build();
    }
}
