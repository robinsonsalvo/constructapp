-- V1: Esquema inicial de ms-cotizacion
-- Generado a partir de las entidades JPA (Cotizacion, DetalleCotizacion) para reemplazar ddl-auto en produccion.

CREATE TABLE cotizaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    fecha_creacion DATE NOT NULL,
    estado VARCHAR(255) NOT NULL,
    precio_total_materiales DOUBLE,
    precio_total_servicios DOUBLE,
    precio_total DOUBLE
);

CREATE TABLE detalles_cotizacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cotizacion_id BIGINT NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    referencia_id BIGINT NOT NULL,
    cantidad DOUBLE NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT fk_detalle_cotizacion FOREIGN KEY (cotizacion_id) REFERENCES cotizaciones(id)
);
