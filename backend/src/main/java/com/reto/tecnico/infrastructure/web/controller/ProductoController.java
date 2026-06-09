package com.reto.tecnico.infrastructure.web.controller;

import com.reto.tecnico.application.dto.request.ProductoRequest;
import com.reto.tecnico.application.dto.response.ApiResponse;
import com.reto.tecnico.application.dto.response.ProductoResponse;
import com.reto.tecnico.application.service.impl.ProductoServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoServiceImpl productoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> findAll(
            @RequestParam(required = false) String empresaNit) {
        List<ProductoResponse> result = empresaNit != null
                ? productoService.findByEmpresa(empresaNit)
                : productoService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> create(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Producto creado", productoService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", productoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado", null));
    }
}
