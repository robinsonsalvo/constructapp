package com.constructapp.ms_cotizacion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalles_cotizacion")
public class DetalleCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDetalle tipo;

    @NotNull(message = "La referencia no puede ser nula")
    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @NotNull(message = "La cantidad no puede ser nula")
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private Double cantidad;

    @NotNull(message = "El precio unitario no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Double subtotal;

    @Column(length = 500)
    private String descripcion;
}