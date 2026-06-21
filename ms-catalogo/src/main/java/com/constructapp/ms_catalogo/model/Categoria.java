package com.constructapp.ms_catalogo.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Column(nullable = false, unique = true)
    private String nombre;

    @NotBlank(message = "la descripcion no puede ir vacia")
    @Column(length = 500)
    private String descripcion;

}
