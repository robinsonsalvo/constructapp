package com.constructapp.ms_calculo_material.exception;

/**
 * Excepcion especifica para recursos no encontrados (HTTP 404).
 * Se utiliza cuando una entidad buscada por ID (u otro criterio unico)
 * no existe en la base de datos, o cuando un recurso referenciado
 * en otro microservicio no existe.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
