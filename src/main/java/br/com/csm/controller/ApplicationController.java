package br.com.csm.controller;

import br.com.csm.dto.ApplicationDTO;
import br.com.csm.dto.UserDTO;
import br.com.csm.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<ApplicationDTO.ApplicationList>> listAllApplications() {
        List<ApplicationDTO.ApplicationList> applications = applicationService.listAllApplications();
        return ResponseEntity.status(HttpStatus.OK).body(applications);
    }

    @PostMapping
    public ResponseEntity<ApplicationDTO.CreateResponse> createApplication (@Valid @RequestBody ApplicationDTO.CreateRequest request) {

        ApplicationDTO.CreateResponse response = applicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateApplication(@PathVariable UUID id, @RequestBody ApplicationDTO.ApplicationUpdate request) {
        applicationService.updateApplication(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Sucesso, sem corpo de resposta
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
    }
}
