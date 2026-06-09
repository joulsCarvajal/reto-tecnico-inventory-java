package com.reto.tecnico.infrastructure.persistence.repository;

import com.reto.tecnico.infrastructure.persistence.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, String> {
    boolean existsByNombre(String nombre);
    List<EmpresaEntity> findAllByOrderByNombreAsc();
}
