package com.reto.tecnico.infrastructure.persistence.entity;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class OrdenProductoId implements Serializable {
    private Long orden;
    private Long producto;
}
