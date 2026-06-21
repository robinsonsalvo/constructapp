package com.constructapp.ms_calculo_material.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CalculoMaterialDTO {

    private Long id;

    @NotNull(message = "El id del proyecto no puede ser nulo")
    private Long proyectoId;

    @NotNull(message = "El id del material no puede ser nulo")
    private Long materialId;

    private String nombreMaterial;

    @NotNull(message = "La cantidad no puede ser nula")
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor a 0")
    private Double cantidadCalculada;

    @NotBlank(message = "La unidad de medida no puede estar vacía")
    private String unidadMedida;

    private Double precioEstimado;

    private String observacion;
}