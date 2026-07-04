-- V1: Esquema inicial de ms-resena
-- Generado a partir de la entidad JPA (Resena) para reemplazar ddl-auto en produccion.

CREATE TABLE resenas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    proveedor_servicio_id BIGINT NOT NULL,
    puntuacion INT NOT NULL,
    comentario VARCHAR(1000) NOT NULL,
    fecha_resena DATE NOT NULL
);
