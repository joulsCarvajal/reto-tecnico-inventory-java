package com.reto.tecnico.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnviarInventarioRequest {
    @Email @NotBlank
    private String destinatario;
    @NotBlank
    private String empresaNit;
    private String asunto;
}
