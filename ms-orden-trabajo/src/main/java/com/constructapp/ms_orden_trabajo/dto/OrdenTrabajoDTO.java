package com.constructapp.ms_orden_trabajo.dto;

import com.constructapp.ms_orden_trabajo.model.EstadoOrden;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OrdenTrabajoDTO {

    private Long id;

    @NotNull(message = "El id de la cotizacion no puede ser nulo")
    private Long cotizacionId;

    @NotNull(message = "El id del proyecto no puede ser nulo")
    private Long proyectoId;

    private EstadoOrden estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;
}