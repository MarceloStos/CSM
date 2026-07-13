package br.com.csm.dto.error;


import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public class ApiErrorResponseDTO {
    private OffsetDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
