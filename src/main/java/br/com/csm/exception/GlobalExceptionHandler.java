package br.com.csm.exception;

import br.com.csm.dto.error.ApiErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiErrorResponseDTO error = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    // Caso o usuario tenta acessar uma rota interna sem o token JWT
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleSpringSecurityAuthenticationException(
            AuthenticationException ex, HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiErrorResponseDTO error = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Acesso negado. Token ausente ou inválido")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponseDTO error = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request){

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiErrorResponseDTO error = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error("Validation error")
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAllUncaughtExceptions(Exception ex, HttpServletRequest request) {

        ex.getStackTrace();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiErrorResponseDTO error = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Ocorreu um erro inesperado no servidor. Contate o administrador do sistema")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}
