-- V1: Esquema inicial de ms-calculo-material
-- Generado a partir de la entidad JPA (CalculoMaterial) para reemplazar ddl-auto en produccion.

CREATE TABLE calculos_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    cantidad_calculada DOUBLE NOT NULL,
    unidad_medida VARCHAR(255) NOT NULL,
    precio_estimado DOUBLE,
    observacion VARCHAR(500)
);
