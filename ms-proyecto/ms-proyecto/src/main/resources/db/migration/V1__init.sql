-- V1: Esquema inicial de ms-proyecto
-- Generado a partir de la entidad JPA (Proyecto) para reemplazar ddl-auto en produccion.

CREATE TABLE proyectos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    tipo VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    cliente_id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_estimada_fin DATE
);
