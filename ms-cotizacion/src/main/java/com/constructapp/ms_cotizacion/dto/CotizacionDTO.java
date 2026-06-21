package com.constructapp.ms_cotizacion.dto;

import com.constructapp.ms_cotizacion.model.EstadoCotizacion;
import com.constructapp.ms_cotizacion.dto.DetalleCotizacionDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CotizacionDTO {

    private Long id;

    @NotNull(message = "El id del proyecto no puede ser nulo")
    private Long proyectoId;

    @NotNull(message = "El id del cliente no puede ser nulo")
    private Long clienteId;

    private LocalDate fechaCreacion;
    private EstadoCotizacion estado;
    private Double precioTotalMateriales;
    private Double precioTotalServicios;
    private Double precioTotal;
    private List<DetalleCotizacionDTO> detalles;
}