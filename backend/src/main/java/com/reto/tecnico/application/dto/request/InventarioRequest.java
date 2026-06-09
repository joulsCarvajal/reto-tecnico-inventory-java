package com.reto.tecnico.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioRequest {
    @NotBlank
    private String empresaNit;
    @NotNull
    private Long productoId;
    @NotNull @Min(0)
    private Integer cantidad;
}
