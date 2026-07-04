-- V1: Esquema inicial de ms-orden-trabajo
-- Generado a partir de la entidad JPA (OrdenTrabajo) para reemplazar ddl-auto en produccion.

CREATE TABLE ordenes_trabajo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cotizacion_id BIGINT NOT NULL UNIQUE,
    proyecto_id BIGINT NOT NULL,
    estado VARCHAR(255) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    observaciones VARCHAR(500)
);
