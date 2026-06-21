package com.constructapp.ms_proveedor_material.dto;

import com.constructapp.ms_proveedor_material.model.Region;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProveedorMaterialDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El RUT no puede estar vacío")
    private String rut;


    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefono;


    private String direccion;


    @NotNull(message = "La región no puede ser nula")
    private Region region;
}