package com.reto.tecnico.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "producto_precio",
       uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "moneda"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductoPrecioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda;

    @Column(name = "precio", nullable = false, precision = 15, scale = 2)
    private BigDecimal precio;
}
