package com.reto.tecnico.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmpresaResponse {
    private String nit;
    private String nombre;
    private String direccion;
    private String telefono;
    private LocalDateTime createdAt;
}
