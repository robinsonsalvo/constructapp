-- V1: Esquema inicial de ms-cliente
-- Generado a partir de la entidad JPA (Cliente) para reemplazar ddl-auto en produccion.

CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    rut VARCHAR(255),
    direccion VARCHAR(500)
);
