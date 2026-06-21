package com.constructapp.ms_auth.dto;

import com.constructapp.ms_auth.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "El username no puede estar vacío")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @NotNull(message = "El rol no puede ser nulo")
    private Rol rol;
}