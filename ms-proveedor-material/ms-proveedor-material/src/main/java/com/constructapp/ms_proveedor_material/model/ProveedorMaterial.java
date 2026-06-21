package com.constructapp.ms_proveedor_material.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table (name ="proveedores_material")
public class ProveedorMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Column(nullable = false, unique = true)
    private String nombre;

    @NotBlank(message = "El rut no puede estar vacio")
    @Column(nullable = false, unique = true)
    private String rut;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "EL email debe tener un formato valido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El telefono  no puede estar vacio")
    @Column(nullable = false)
    private String telefono;

    @Column(length = 500)
    private String direccion;

    @NotNull(message = "la region no puede estar vacio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;
}
