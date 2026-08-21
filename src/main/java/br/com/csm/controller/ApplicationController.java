package br.com.csm.controller;

import br.com.csm.dto.ApplicationDTO;
import br.com.csm.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    public ResponseEntity<List<ApplicationDTO.ApplicationList>> listAll() {
        List<ApplicationDTO.ApplicationList> applications = applicationService.listAll();
        return ResponseEntity.status(HttpStatus.OK).body(applications);
    }

    @PostMapping
    public ResponseEntity<ApplicationDTO.CreateResponse> createApplication (@Valid @RequestBody ApplicationDTO.CreateRequest request) {

        ApplicationDTO.CreateResponse response = applicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
