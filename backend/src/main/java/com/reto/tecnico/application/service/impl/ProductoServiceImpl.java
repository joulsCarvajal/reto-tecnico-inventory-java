package com.reto.tecnico.application.service.impl;

import com.reto.tecnico.application.dto.request.ProductoRequest;
import com.reto.tecnico.application.dto.response.CategoriaResponse;
import com.reto.tecnico.application.dto.response.ProductoPrecioResponse;
import com.reto.tecnico.application.dto.response.ProductoResponse;
import com.reto.tecnico.infrastructure.persistence.entity.*;
import com.reto.tecnico.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponse> findAll() {
        return productoRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> findByEmpresa(String empresaNit) {
        return productoRepository.findByEmpresaNit(empresaNit).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public ProductoResponse create(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con código: " + request.getCodigo());
        }
        EmpresaEntity empresa = empresaRepository.findById(request.getEmpresaNit())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + request.getEmpresaNit()));

        ProductoEntity producto = ProductoEntity.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .caracteristicas(request.getCaracteristicas())
                .empresa(empresa)
                .build();

        addPrecios(producto, request);
        addCategorias(producto, request);

        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {
        ProductoEntity producto = getOrThrow(id);

        if (!producto.getCodigo().equals(request.getCodigo()) &&
                productoRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con código: " + request.getCodigo());
        }

        EmpresaEntity empresa = empresaRepository.findById(request.getEmpresaNit())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + request.getEmpresaNit()));

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setCaracteristicas(request.getCaracteristicas());
        producto.setEmpresa(empresa);
        producto.getPrecios().clear();
        producto.getCategorias().clear();
        productoRepository.saveAndFlush(producto); // flush DELETEs before INSERTs
        addPrecios(producto, request);
        addCategorias(producto, request);

        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado: " + id);
        }
        productoRepository.deleteById(id);
    }

    private void addPrecios(ProductoEntity producto, ProductoRequest request) {
        if (request.getPrecios() != null) {
            request.getPrecios().forEach((moneda, precio) -> {
                ProductoPrecioEntity pp = ProductoPrecioEntity.builder()
                        .producto(producto).moneda(moneda).precio(precio).build();
                producto.getPrecios().add(pp);
            });
        }
    }

    private void addCategorias(ProductoEntity producto, ProductoRequest request) {
        if (request.getCategoriaIds() != null) {
            Set<CategoriaEntity> cats = new HashSet<>(categoriaRepository.findAllById(request.getCategoriaIds()));
            producto.getCategorias().addAll(cats);
        }
    }

    private ProductoEntity getOrThrow(Long id) {
        return productoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    public ProductoResponse toResponse(ProductoEntity p) {
        List<ProductoPrecioResponse> precios = p.getPrecios().stream()
                .map(pp -> new ProductoPrecioResponse(pp.getMoneda(), pp.getPrecio()))
                .collect(Collectors.toList());
        List<CategoriaResponse> cats = p.getCategorias().stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion()))
                .collect(Collectors.toList());
        return ProductoResponse.builder()
                .id(p.getId())
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .caracteristicas(p.getCaracteristicas())
                .empresaNit(p.getEmpresa().getNit())
                .empresaNombre(p.getEmpresa().getNombre())
                .precios(precios)
                .categorias(cats)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
