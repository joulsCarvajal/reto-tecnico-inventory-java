package com.reto.tecnico.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventarioResponse {
    private Long id;
    private String empresaNit;
    private String empresaNombre;
    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private String productoCaracteristicas;
    private Integer cantidad;
    private List<ProductoPrecioResponse> precios;
    private LocalDateTime updatedAt;
}
