package com.reto.tecnico.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductoRequest {
    @NotBlank @Size(max = 50)
    private String codigo;
    @NotBlank @Size(max = 150)
    private String nombre;
    private String caracteristicas;
    @NotBlank
    private String empresaNit;
    private Map<String, BigDecimal> precios;   // {"COP": 150000, "USD": 37}
    private List<Long> categoriaIds;
}
