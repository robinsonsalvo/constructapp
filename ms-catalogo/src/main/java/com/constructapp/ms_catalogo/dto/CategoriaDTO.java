package com.constructapp.ms_catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;
    @NotBlank(message = "La descripcion no puede estar vacia")
    private String descripcion;

}
