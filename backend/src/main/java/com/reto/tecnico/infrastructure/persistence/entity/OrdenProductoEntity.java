package com.reto.tecnico.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orden_producto")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@IdClass(OrdenProductoId.class)
public class OrdenProductoEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenEntity orden;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(name = "cantidad", nullable = false)
    @Builder.Default
    private Integer cantidad = 1;

    @Column(name = "precio_unit", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioUnit;
}
