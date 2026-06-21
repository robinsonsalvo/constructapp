package com.constructapp.ms_proyecto.dto;


import com.constructapp.ms_proyecto.model.EstadoProyecto;
import com.constructapp.ms_proyecto.model.TipoProyecto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProyectoDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tipo no puede ser nulo")
    private TipoProyecto tipo;

    private EstadoProyecto estado;

    @NotNull(message = "El clienteId no puede ser nulo")
    private Long clienteId;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    private LocalDate fechaInicio;

    private LocalDate fechaEstimadaFin;
}
