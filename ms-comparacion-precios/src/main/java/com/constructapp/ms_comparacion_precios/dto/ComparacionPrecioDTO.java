package com.constructapp.ms_comparacion_precios.dto;

import lombok.Data;

@Data
public class ComparacionPrecioDTO {
    private Long precioProveedorId;
    private Long materialId;
    private String nombreProveedor;
    private Double precio;
    private Integer stockDisponible;
    private Long proveedorId;
}