package com.constructapp.ms_proveedor_servicio.dto;


import com.constructapp.ms_proveedor_servicio.model.Modalidad;
import com.constructapp.ms_proveedor_servicio.model.TipoServicio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class ProveedorServicioDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotBlank(message = "El RUT no puede estar vacío")
    private String rut;

    @NotNull(message = "El tipo de servicio no puede ser nulo")
    private TipoServicio tipoServicio;

    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "La modalidad no puede ser nula")
    private Modalidad modalidad;

    @NotBlank(message = "La región no puede estar vacía")
    private String region;

    private Boolean disponible;
}
