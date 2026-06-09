package com.reto.tecnico.infrastructure.persistence.repository;

import com.reto.tecnico.infrastructure.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    boolean existsByCodigo(String codigo);
    Optional<ProductoEntity> findByCodigo(String codigo);
    List<ProductoEntity> findByEmpresaNit(String empresaNit);

    @Query("SELECT p FROM ProductoEntity p LEFT JOIN FETCH p.precios LEFT JOIN FETCH p.categorias WHERE p.id = :id")
    Optional<ProductoEntity> findByIdWithDetails(Long id);
}
