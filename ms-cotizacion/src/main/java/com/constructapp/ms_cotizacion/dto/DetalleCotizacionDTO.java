package com.constructapp.ms_cotizacion.dto;

import com.constructapp.ms_cotizacion.model.TipoDetalle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleCotizacionDTO {

    private Long id;

    @NotNull(message = "El tipo no puede ser nulo")
    private TipoDetalle tipo;

    @NotNull(message = "La referencia no puede ser nula")
    private Long referenciaId;

    @NotNull(message = "La cantidad no puede ser nula")
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor a 0")
    private Double cantidad;

    @NotNull(message = "El precio unitario no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precioUnitario;

    private Double subtotal;
    private String descripcion;
    private Long cotizacionId;
}