package com.constructapp.ms_resena.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del cliente no puede ser nulo")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotNull(message = "El id del proveedor no puede ser nulo")
    @Column(name = "proveedor_servicio_id", nullable = false)
    private Long proveedorServicioId;

    @NotNull(message = "La puntuación no puede ser nula")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    @Column(nullable = false)
    private Integer puntuacion;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Column(length = 1000, nullable = false)
    private String comentario;

    @Column(name = "fecha_resena", nullable = false)
    private LocalDate fechaResena;
}