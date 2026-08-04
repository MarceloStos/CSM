package br.com.csm.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

public class ErrorDTO {

    @Builder
    public record ApiErrorResponse (
            OffsetDateTime timestamp,
            Integer status,
            String error,
            String message,
            String path
    ){}

}
