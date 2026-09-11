package br.com.csm.application;

import br.com.csm.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<ApplicationSummaryResponse>> listAllApplications() {
        List<ApplicationSummaryResponse> applications = applicationService.listAllApplications();
        return ResponseEntity.status(HttpStatus.OK).body(applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDetailsResponse> viewApplication(@PathVariable UUID id) {
        ApplicationDetailsResponse application = applicationService.getApplicationById(id);
        return ResponseEntity.status(HttpStatus.OK).body(application);
    }

    @PostMapping
    public ResponseEntity<ApplicationCreateResponse> createApplication (@Valid @RequestBody ApplicationCreateRequest request) {

        ApplicationCreateResponse response = applicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateApplication(@PathVariable UUID id, @RequestBody ApplicationUpdateRequest request) {
        applicationService.updateApplication(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Sucesso, sem corpo de resposta
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }
}
