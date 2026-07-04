-- V1: Esquema inicial de ms-proveedor-material
-- Generado a partir de las entidades JPA (ProveedorMaterial, PrecioProveedor) para reemplazar ddl-auto en produccion.

CREATE TABLE proveedores_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    rut VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(255) NOT NULL,
    direccion VARCHAR(500),
    region VARCHAR(255) NOT NULL
);

CREATE TABLE precios_proveedor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL,
    precio DOUBLE NOT NULL,
    stock_disponible INT NOT NULL,
    proveedor_id BIGINT NOT NULL,
    CONSTRAINT fk_precio_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores_material(id)
);
