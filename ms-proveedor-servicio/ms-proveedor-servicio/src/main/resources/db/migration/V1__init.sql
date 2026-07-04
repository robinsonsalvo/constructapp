-- V1: Esquema inicial de ms-proveedor-servicio
-- Generado a partir de la entidad JPA (ProveedorServicio) para reemplazar ddl-auto en produccion.

CREATE TABLE proveedores_servicio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    rut VARCHAR(255) NOT NULL UNIQUE,
    tipo_servicio VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    precio DOUBLE NOT NULL,
    modalidad VARCHAR(255) NOT NULL,
    region VARCHAR(255) NOT NULL,
    disponible BIT(1) NOT NULL
);
