package com.constructapp.ms_cliente.dto;

import com.constructapp.ms_cliente.model.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClienteDTO {
    
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacio")
    private String apellido;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email no tiene un formato valido")
    private String email;

    @NotBlank(message = "El telefono no puede estar vacio")
    private String telefono;

    @NotNull(message = "El tipo de cliente no puede ser nulo")
    private TipoCliente tipo;

    private String rut;

    private String direccion;


}
