package com.constructapp.ms_proveedor_material.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// @Data genera automáticamente getters y setters para todos los campos
@Data
public class PrecioProveedorDTO {

    // Id generado por la BD, no se valida en entrada
    private Long id;

    // Id del material en ms-catalogo, solo guardamos el número
    @NotNull(message = "El id del material no puede ser nulo")
    private Long materialId;

    // Nombre del material, se llena consultando ms-catalogo
    // No se valida porque no lo envía el usuario, lo llenamos nosotros
    private String nombreMaterial;

    // Precio del material para este proveedor
    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, 
                message = "El precio debe ser mayor a 0")
    private Double precio;

    // Cantidad disponible en stock
    // @Min(0) permite 0 pero no negativos
    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockDisponible;

    // Id del proveedor dueño de este precio
    @NotNull(message = "El id del proveedor no puede ser nulo")
    private Long proveedorId;

    // Nombre del proveedor, se llena desde la entidad
    // No se valida porque no lo envía el usuario
    private String nombreProveedor;
}