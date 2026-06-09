-- ============================================================
-- V2 - Datos iniciales
-- ============================================================

-- Usuarios iniciales
-- Contraseñas encriptadas con BCrypt (bcrypt strength 10)
-- admin@reto.com  -> password: Admin123!
-- externo@reto.com -> password: Externo123!

INSERT INTO usuario (correo, password, nombre, rol) VALUES
    ('admin@reto.com',
     '$2a$10$GHhTOYIpBagXKU6RIBYfJOo44E9ZpnVR0C3NEyv6f1ROqyuwc7j9C',
     'Administrador Sistema', 'ADMINISTRADOR'),
    ('externo@reto.com',
     '$2a$10$U5X8bPJRS67Fq0rEAadtaunh7GY9qcCjCZq5rGXh8OlzKuJ.UIT46',
     'Usuario Externo', 'EXTERNO');

-- Categorías base
INSERT INTO categoria (nombre, descripcion) VALUES
    ('Electrónica',     'Dispositivos y componentes electrónicos'),
    ('Hogar',           'Artículos para el hogar'),
    ('Oficina',         'Suministros y equipos de oficina'),
    ('Tecnología',      'Software y hardware tecnológico'),
    ('Alimentación',    'Productos alimenticios');

-- Empresa de ejemplo
INSERT INTO empresa (nit, nombre, direccion, telefono) VALUES
    ('900123456-1', 'Tech Solutions S.A.S', 'Calle 123 # 45-67, Bogotá', '6013456789'),
    ('800987654-2', 'Comercial Andina Ltda', 'Carrera 7 # 32-10, Medellín', '6044567890');

-- Productos de ejemplo
INSERT INTO producto (codigo, nombre, caracteristicas, empresa_nit) VALUES
    ('PROD-001', 'Laptop Empresarial', 'Intel Core i7, 16GB RAM, 512GB SSD, Windows 11 Pro', '900123456-1'),
    ('PROD-002', 'Monitor 27"', '4K UHD, 144Hz, IPS, 1ms, HDMI/DisplayPort', '900123456-1'),
    ('PROD-003', 'Silla Ergonómica', 'Soporte lumbar ajustable, apoyabrazos 4D, altura ajustable', '800987654-2');

-- Precios en varias monedas
INSERT INTO producto_precio (producto_id, moneda, precio) VALUES
    (1, 'COP', 3500000.00),
    (1, 'USD', 850.00),
    (1, 'EUR', 790.00),
    (2, 'COP', 1200000.00),
    (2, 'USD', 290.00),
    (2, 'EUR', 270.00),
    (3, 'COP', 650000.00),
    (3, 'USD', 158.00),
    (3, 'EUR', 147.00);

-- Categorías de productos
INSERT INTO producto_categoria (producto_id, categoria_id) VALUES
    (1, 1), (1, 4),  -- Laptop: Electrónica, Tecnología
    (2, 1),          -- Monitor: Electrónica
    (3, 2), (3, 3);  -- Silla: Hogar, Oficina

-- Inventario inicial
INSERT INTO inventario (empresa_nit, producto_id, cantidad) VALUES
    ('900123456-1', 1, 10),
    ('900123456-1', 2, 25),
    ('800987654-2', 3, 15);

-- Cliente de ejemplo
INSERT INTO cliente (nombre, correo, telefono, direccion) VALUES
    ('Juan Pérez', 'juan.perez@email.com', '3101234567', 'Calle 50 # 20-30, Bogotá');
