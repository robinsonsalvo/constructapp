-- V1: Esquema inicial de ms-catalogo
-- Generado a partir de las entidades JPA (Categoria, Material) para reemplazar ddl-auto en produccion.

CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(500)
);

CREATE TABLE materiales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    unidad_medida VARCHAR(255) NOT NULL,
    precio_referencial DOUBLE NOT NULL,
    descripcion VARCHAR(500),
    categoria_id BIGINT NOT NULL,
    CONSTRAINT fk_materiales_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);
