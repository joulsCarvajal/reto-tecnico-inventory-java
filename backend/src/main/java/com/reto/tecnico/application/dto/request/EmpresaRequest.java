package com.reto.tecnico.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmpresaRequest {
    @NotBlank @Size(max = 20)
    private String nit;
    @NotBlank @Size(max = 150)
    private String nombre;
    @NotBlank @Size(max = 255)
    private String direccion;
    @NotBlank @Size(max = 20)
    private String telefono;
}
