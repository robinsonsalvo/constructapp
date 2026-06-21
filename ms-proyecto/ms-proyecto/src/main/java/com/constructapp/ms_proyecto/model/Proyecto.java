package com.constructapp.ms_proyecto.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "El tipo no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProyecto tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProyecto estado = EstadoProyecto.COTIZANDO;

    @NotNull(message = "El clienteId no puede ser nulo")
    @Column(nullable = false)
    private Long clienteId;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaEstimadaFin;

}
