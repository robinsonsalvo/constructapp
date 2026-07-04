-- V1: Esquema inicial de ms-auth
-- Generado a partir de las entidades JPA (Usuario) para reemplazar ddl-auto en produccion.

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL
);
