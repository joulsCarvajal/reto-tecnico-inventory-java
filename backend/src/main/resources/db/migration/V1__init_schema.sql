-- ============================================================
-- V1 - Esquema inicial
-- ============================================================

-- Tabla: empresa
CREATE TABLE empresa (
    nit         VARCHAR(20)  PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    direccion   VARCHAR(255) NOT NULL,
    telefono    VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabla: categoria
CREATE TABLE categoria (
    id          BIGSERIAL    PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabla: producto
CREATE TABLE producto (
    id              BIGSERIAL    PRIMARY KEY,
    codigo          VARCHAR(50)  NOT NULL UNIQUE,
    nombre          VARCHAR(150) NOT NULL,
    caracteristicas TEXT,
    empresa_nit     VARCHAR(20)  NOT NULL REFERENCES empresa(nit) ON DELETE CASCADE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabla: producto_precio (precio en varias monedas)
CREATE TABLE producto_precio (
    id          BIGSERIAL    PRIMARY KEY,
    producto_id BIGINT       NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    moneda      VARCHAR(10)  NOT NULL,  -- COP, USD, EUR, etc.
    precio      NUMERIC(15,2) NOT NULL,
    UNIQUE (producto_id, moneda)
);

-- Tabla: producto_categoria (many-to-many)
CREATE TABLE producto_categoria (
    producto_id  BIGINT NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    categoria_id BIGINT NOT NULL REFERENCES categoria(id) ON DELETE CASCADE,
    PRIMARY KEY (producto_id, categoria_id)
);

-- Tabla: inventario
CREATE TABLE inventario (
    id          BIGSERIAL    PRIMARY KEY,
    empresa_nit VARCHAR(20)  NOT NULL REFERENCES empresa(nit) ON DELETE CASCADE,
    producto_id BIGINT       NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    cantidad    INTEGER      NOT NULL DEFAULT 0 CHECK (cantidad >= 0),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (empresa_nit, producto_id)
);

-- Tabla: usuario
CREATE TABLE usuario (
    id          BIGSERIAL    PRIMARY KEY,
    correo      VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nombre      VARCHAR(100) NOT NULL,
    rol         VARCHAR(20)  NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'EXTERNO')),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabla: cliente
CREATE TABLE cliente (
    id          BIGSERIAL    PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    correo      VARCHAR(150) NOT NULL UNIQUE,
    telefono    VARCHAR(20),
    direccion   VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabla: orden
CREATE TABLE orden (
    id          BIGSERIAL    PRIMARY KEY,
    cliente_id  BIGINT       NOT NULL REFERENCES cliente(id),
    estado      VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE'
                    CHECK (estado IN ('PENDIENTE', 'PROCESANDO', 'COMPLETADA', 'CANCELADA')),
    total       NUMERIC(15,2) NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabla: orden_producto (many-to-many con cantidad)
CREATE TABLE orden_producto (
    orden_id    BIGINT        NOT NULL REFERENCES orden(id) ON DELETE CASCADE,
    producto_id BIGINT        NOT NULL REFERENCES producto(id),
    cantidad    INTEGER       NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    precio_unit NUMERIC(15,2) NOT NULL,
    PRIMARY KEY (orden_id, producto_id)
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_producto_empresa ON producto(empresa_nit);
CREATE INDEX idx_inventario_empresa ON inventario(empresa_nit);
CREATE INDEX idx_orden_cliente ON orden(cliente_id);
CREATE INDEX idx_orden_estado ON orden(estado);
