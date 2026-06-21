package com.constructapp.ms_orden_trabajo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "ordenes_trabajo")
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id de la cotizacion no puede ser nulo")
    @Column(name = "cotizacion_id", nullable = false, unique = true)
    private Long cotizacionId;

    @NotNull(message = "El id del proyecto no puede ser nulo")
    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(length = 500)
    private String observaciones;
}