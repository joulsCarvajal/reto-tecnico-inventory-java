package com.reto.tecnico.infrastructure.web.controller;

import com.reto.tecnico.application.dto.response.ApiResponse;
import com.reto.tecnico.application.dto.response.CategoriaResponse;
import com.reto.tecnico.infrastructure.persistence.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> findAll() {
        List<CategoriaResponse> cats = categoriaRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(cats));
    }
}
