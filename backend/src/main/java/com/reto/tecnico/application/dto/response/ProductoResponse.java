package com.reto.tecnico.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductoResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String caracteristicas;
    private String empresaNit;
    private String empresaNombre;
    private List<ProductoPrecioResponse> precios;
    private List<CategoriaResponse> categorias;
    private LocalDateTime createdAt;
}
