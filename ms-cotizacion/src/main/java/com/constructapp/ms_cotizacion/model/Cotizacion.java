package com.constructapp.ms_cotizacion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "cotizaciones")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del proyecto no puede ser nulo")
    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @NotNull(message = "El id del cliente no puede ser nulo")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCotizacion estado;

    @Column(name = "precio_total_materiales")
    private Double precioTotalMateriales;

    @Column(name = "precio_total_servicios")
    private Double precioTotalServicios;

    @Column(name = "precio_total")
    private Double precioTotal;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DetalleCotizacion> detalles;
}