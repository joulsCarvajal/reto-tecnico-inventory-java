package com.reto.tecnico.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Column(name = "estado", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Estado estado = Estado.PENDIENTE;

    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrdenProductoEntity> items = new ArrayList<>();

    public enum Estado {
        PENDIENTE, PROCESANDO, COMPLETADA, CANCELADA
    }
}
