package com.constructapp.ms_auth.exception;

/**
 * Excepcion especifica para credenciales de acceso invalidas (HTTP 401).
 * Se utiliza cuando el username no existe o la contraseña no coincide
 * durante el proceso de login.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
