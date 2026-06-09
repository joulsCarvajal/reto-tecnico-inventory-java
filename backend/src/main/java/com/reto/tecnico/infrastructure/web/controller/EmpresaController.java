package com.reto.tecnico.infrastructure.web.controller;

import com.reto.tecnico.application.dto.request.EmpresaRequest;
import com.reto.tecnico.application.dto.response.ApiResponse;
import com.reto.tecnico.application.dto.response.EmpresaResponse;
import com.reto.tecnico.application.service.impl.EmpresaServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaServiceImpl empresaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpresaResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.findAll()));
    }

    @GetMapping("/{nit}")
    public ResponseEntity<ApiResponse<EmpresaResponse>> findByNit(@PathVariable String nit) {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.findByNit(nit)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmpresaResponse>> create(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Empresa creada exitosamente", empresaService.create(request)));
    }

    @PutMapping("/{nit}")
    public ResponseEntity<ApiResponse<EmpresaResponse>> update(@PathVariable String nit,
                                                                @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Empresa actualizada", empresaService.update(nit, request)));
    }

    @DeleteMapping("/{nit}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String nit) {
        empresaService.delete(nit);
        return ResponseEntity.ok(ApiResponse.ok("Empresa eliminada", null));
    }
}
