package com.constructapp.ms_resena.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ResenaDTO {

    private Long id;

    @NotNull(message = "El id del cliente no puede ser nulo")
    private Long clienteId;

    @NotNull(message = "El id del proveedor no puede ser nulo")
    private Long proveedorServicioId;

    @NotNull(message = "La puntuación no puede ser nula")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer puntuacion;

    @NotBlank(message = "El comentario no puede estar vacío")
    private String comentario;

    private LocalDate fechaResena;
}