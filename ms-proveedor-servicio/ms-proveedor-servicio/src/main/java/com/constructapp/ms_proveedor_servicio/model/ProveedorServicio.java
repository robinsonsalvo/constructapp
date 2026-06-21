package com.constructapp.ms_proveedor_servicio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
@Entity
@Table(name = "proveedores_servicio")
public class ProveedorServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El RUT no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String rut;

    @NotNull(message = "El tipo de servicio no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoServicio tipoServicio;

    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "La modalidad no puede ser nula")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidad modalidad;

    @NotBlank(message = "La región no puede estar vacía")
    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private Boolean disponible = true;
    
}
