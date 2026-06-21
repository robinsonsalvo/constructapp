package com.constructapp.ms_calculo_material.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "calculos_material")
public class CalculoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del proyecto no puede ser nulo")
    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @NotNull(message = "El id del material no puede ser nulo")
    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @NotNull(message = "La cantidad no puede ser nula")
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor a 0")
    @Column(name = "cantidad_calculada", nullable = false)
    private Double cantidadCalculada;

    @NotBlank(message = "La unidad de medida no puede estar vacía")
    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @Column(name = "precio_estimado")
    private Double precioEstimado;

    @Column(length = 500)
    private String observacion;
}