package com.reto.tecnico.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "rol", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Rol {
        ADMINISTRADOR, EXTERNO
    }
}
