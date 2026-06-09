package com.reto.tecnico.application.service.impl;

import com.reto.tecnico.application.dto.request.InventarioRequest;
import com.reto.tecnico.application.dto.response.InventarioResponse;
import com.reto.tecnico.application.dto.response.ProductoPrecioResponse;
import com.reto.tecnico.infrastructure.persistence.entity.EmpresaEntity;
import com.reto.tecnico.infrastructure.persistence.entity.InventarioEntity;
import com.reto.tecnico.infrastructure.persistence.entity.ProductoEntity;
import com.reto.tecnico.infrastructure.persistence.repository.EmpresaRepository;
import com.reto.tecnico.infrastructure.persistence.repository.InventarioRepository;
import com.reto.tecnico.infrastructure.persistence.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl {

    private final InventarioRepository inventarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<InventarioResponse> findByEmpresa(String empresaNit) {
        return inventarioRepository.findByEmpresaNitWithDetails(empresaNit)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public InventarioResponse upsert(InventarioRequest request) {
        EmpresaEntity empresa = empresaRepository.findById(request.getEmpresaNit())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + request.getEmpresaNit()));
        ProductoEntity producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.getProductoId()));

        InventarioEntity inventario = inventarioRepository
                .findByEmpresaNitAndProductoId(request.getEmpresaNit(), request.getProductoId())
                .orElseGet(() -> InventarioEntity.builder()
                        .empresa(empresa).producto(producto).cantidad(0).build());

        inventario.setCantidad(request.getCantidad());
        return toResponse(inventarioRepository.save(inventario));
    }

    @Transactional
    public void delete(Long id) {
        if (!inventarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Registro de inventario no encontrado: " + id);
        }
        inventarioRepository.deleteById(id);
    }

    public InventarioResponse toResponse(InventarioEntity i) {
        List<ProductoPrecioResponse> precios = i.getProducto().getPrecios().stream()
                .map(pp -> new ProductoPrecioResponse(pp.getMoneda(), pp.getPrecio()))
                .collect(Collectors.toList());

        return InventarioResponse.builder()
                .id(i.getId())
                .empresaNit(i.getEmpresa().getNit())
                .empresaNombre(i.getEmpresa().getNombre())
                .productoId(i.getProducto().getId())
                .productoCodigo(i.getProducto().getCodigo())
                .productoNombre(i.getProducto().getNombre())
                .productoCaracteristicas(i.getProducto().getCaracteristicas())
                .cantidad(i.getCantidad())
                .precios(precios)
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
