package br.com.csm.controller;

import br.com.csm.dto.application.CreateRequestDTO;
import br.com.csm.dto.application.CreateResponseDTO;
import br.com.csm.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<CreateResponseDTO> createApplication (@Valid @RequestBody CreateRequestDTO request) {

        CreateResponseDTO response = applicationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
