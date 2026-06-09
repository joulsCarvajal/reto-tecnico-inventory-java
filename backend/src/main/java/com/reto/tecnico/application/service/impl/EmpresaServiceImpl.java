package com.reto.tecnico.application.service.impl;

import com.reto.tecnico.application.dto.request.EmpresaRequest;
import com.reto.tecnico.application.dto.response.EmpresaResponse;
import com.reto.tecnico.infrastructure.persistence.entity.EmpresaEntity;
import com.reto.tecnico.infrastructure.persistence.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl {

    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<EmpresaResponse> findAll() {
        return empresaRepository.findAllByOrderByNombreAsc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaResponse findByNit(String nit) {
        return toResponse(getOrThrow(nit));
    }

    @Transactional
    public EmpresaResponse create(EmpresaRequest request) {
        if (empresaRepository.existsById(request.getNit())) {
            throw new IllegalArgumentException("Ya existe una empresa con NIT: " + request.getNit());
        }
        EmpresaEntity entity = EmpresaEntity.builder()
                .nit(request.getNit())
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .build();
        return toResponse(empresaRepository.save(entity));
    }

    @Transactional
    public EmpresaResponse update(String nit, EmpresaRequest request) {
        EmpresaEntity entity = getOrThrow(nit);
        entity.setNombre(request.getNombre());
        entity.setDireccion(request.getDireccion());
        entity.setTelefono(request.getTelefono());
        return toResponse(empresaRepository.save(entity));
    }

    @Transactional
    public void delete(String nit) {
        if (!empresaRepository.existsById(nit)) {
            throw new IllegalArgumentException("Empresa no encontrada: " + nit);
        }
        empresaRepository.deleteById(nit);
    }

    private EmpresaEntity getOrThrow(String nit) {
        return empresaRepository.findById(nit)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + nit));
    }

    private EmpresaResponse toResponse(EmpresaEntity e) {
        return EmpresaResponse.builder()
                .nit(e.getNit())
                .nombre(e.getNombre())
                .direccion(e.getDireccion())
                .telefono(e.getTelefono())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
