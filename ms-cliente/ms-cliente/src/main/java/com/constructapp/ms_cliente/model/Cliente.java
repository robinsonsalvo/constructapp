package com.constructapp.ms_cliente.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacio")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El emailm non puede estar vacio")
    @Email(message = "El email no tiene un formato valido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El telefono no puede estar vacio")
    @Column(nullable = false)
    private String telefono;

    @NotNull(message = "El tipo de cliente no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipo;

    private String rut;

    @Column(length = 500)
    private String direccion;




}
