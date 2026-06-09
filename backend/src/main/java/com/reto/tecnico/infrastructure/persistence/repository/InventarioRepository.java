package com.reto.tecnico.infrastructure.persistence.repository;

import com.reto.tecnico.infrastructure.persistence.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity, Long> {
    List<InventarioEntity> findByEmpresaNit(String empresaNit);
    Optional<InventarioEntity> findByEmpresaNitAndProductoId(String empresaNit, Long productoId);

    @Query("""
        SELECT i FROM InventarioEntity i
        JOIN FETCH i.empresa
        JOIN FETCH i.producto p
        LEFT JOIN FETCH p.precios
        WHERE i.empresa.nit = :empresaNit
        ORDER BY p.nombre ASC
    """)
    List<InventarioEntity> findByEmpresaNitWithDetails(String empresaNit);
}
