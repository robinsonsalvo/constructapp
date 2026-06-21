package com.constructapp.ms_proveedor_material.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "precios_proveedor")
public class PrecioProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del material no puede ser nulo")
    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private ProveedorMaterial proveedor;
}