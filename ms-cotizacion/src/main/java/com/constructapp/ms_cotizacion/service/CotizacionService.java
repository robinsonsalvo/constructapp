package com.constructapp.ms_cotizacion.service;

import com.constructapp.ms_cotizacion.exception.ResourceNotFoundException;

import com.constructapp.ms_cotizacion.dto.CotizacionDTO;
import com.constructapp.ms_cotizacion.dto.DetalleCotizacionDTO;
import com.constructapp.ms_cotizacion.model.*;
import com.constructapp.ms_cotizacion.repository.CotizacionRepository;
import com.constructapp.ms_cotizacion.repository.DetalleCotizacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleRepository;
    private final WebClient webClientCliente;
    private final WebClient webClientProveedorMaterial;
    private final WebClient webClientProveedorServicio;

    public CotizacionService(
            CotizacionRepository cotizacionRepository,
            DetalleCotizacionRepository detalleRepository,
            @Qualifier("webClientCliente") WebClient webClientCliente,
            @Qualifier("webClientProveedorMaterial") WebClient webClientProveedorMaterial,
            @Qualifier("webClientProveedorServicio") WebClient webClientProveedorServicio) {
        this.cotizacionRepository = cotizacionRepository;
        this.detalleRepository = detalleRepository;
        this.webClientCliente = webClientCliente;
        this.webClientProveedorMaterial = webClientProveedorMaterial;
        this.webClientProveedorServicio = webClientProveedorServicio;
    }

    public List<CotizacionDTO> listarTodas() {
        log.info("Listando todas las cotizaciones");
        return cotizacionRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<CotizacionDTO> listarPorCliente(Long clienteId) {
        log.info("Listando cotizaciones del cliente id: {}", clienteId);
        return cotizacionRepository.findByClienteId(clienteId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<CotizacionDTO> listarPorProyecto(Long proyectoId) {
        log.info("Listando cotizaciones del proyecto id: {}", proyectoId);
        return cotizacionRepository.findByProyectoId(proyectoId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public CotizacionDTO obtenerPorId(Long id) {
        log.info("Buscando cotizacion con id: {}", id);
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotizacion no encontrada con id: " + id));
        return convertirADTO(cotizacion);
    }

    public CotizacionDTO crear(CotizacionDTO dto) {
        log.info("Creando cotizacion para cliente id: {}", dto.getClienteId());

        verificarClienteExiste(dto.getClienteId());

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProyectoId(dto.getProyectoId());
        cotizacion.setClienteId(dto.getClienteId());
        cotizacion.setFechaCreacion(LocalDate.now());
        cotizacion.setEstado(EstadoCotizacion.BORRADOR);

        double totalMateriales = 0.0;
        double totalServicios = 0.0;
        List<DetalleCotizacion> detalles = new ArrayList<>();

        if (dto.getDetalles() != null) {
            for (DetalleCotizacionDTO detalleDTO : dto.getDetalles()) {
                DetalleCotizacion detalle = new DetalleCotizacion();
                detalle.setTipo(detalleDTO.getTipo());
                detalle.setReferenciaId(detalleDTO.getReferenciaId());
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                double subtotal = detalleDTO.getCantidad() * detalleDTO.getPrecioUnitario();
                detalle.setSubtotal(subtotal);
                detalle.setDescripcion(detalleDTO.getDescripcion());
                detalle.setCotizacion(cotizacion);

                if (detalleDTO.getTipo() == TipoDetalle.MATERIAL) {
                    totalMateriales += subtotal;
                } else {
                    totalServicios += subtotal;
                }
                detalles.add(detalle);
            }
        }

        cotizacion.setPrecioTotalMateriales(totalMateriales);
        cotizacion.setPrecioTotalServicios(totalServicios);
        cotizacion.setPrecioTotal(totalMateriales + totalServicios);
        cotizacion.setDetalles(detalles);

        Cotizacion guardada = cotizacionRepository.save(cotizacion);
        log.info("Cotizacion creada con id: {}", guardada.getId());
        return convertirADTO(guardada);
    }

    /**
     * Reglas de transición de estado:
     * BORRADOR   → ENVIADA
     * ENVIADA    → APROBADA | RECHAZADA
     * APROBADA   → no puede cambiar (estado final)
     * RECHAZADA  → no puede cambiar (estado final)
     */
    public CotizacionDTO cambiarEstado(Long id, EstadoCotizacion nuevoEstado) {
        log.info("Cambiando estado de cotizacion id: {} a {}", id, nuevoEstado);
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotizacion no encontrada con id: " + id));

        EstadoCotizacion estadoActual = cotizacion.getEstado();
        validarTransicionEstado(estadoActual, nuevoEstado);

        cotizacion.setEstado(nuevoEstado);
        Cotizacion actualizada = cotizacionRepository.save(cotizacion);
        log.info("Estado de cotizacion id: {} cambiado de {} a {}", id, estadoActual, nuevoEstado);
        return convertirADTO(actualizada);
    }

    private void validarTransicionEstado(EstadoCotizacion actual, EstadoCotizacion nuevo) {
        switch (actual) {
            case BORRADOR:
                if (nuevo != EstadoCotizacion.ENVIADA) {
                    throw new RuntimeException(
                        "Una cotizacion en estado BORRADOR solo puede pasar a ENVIADA. Estado solicitado: " + nuevo);
                }
                break;
            case ENVIADA:
                if (nuevo != EstadoCotizacion.APROBADA && nuevo != EstadoCotizacion.RECHAZADA) {
                    throw new RuntimeException(
                        "Una cotizacion ENVIADA solo puede pasar a APROBADA o RECHAZADA. Estado solicitado: " + nuevo);
                }
                break;
            case APROBADA:
                throw new RuntimeException(
                    "Una cotizacion APROBADA no puede cambiar de estado. Es un estado final.");
            case RECHAZADA:
                throw new RuntimeException(
                    "Una cotizacion RECHAZADA no puede cambiar de estado. Es un estado final.");
            default:
                throw new RuntimeException("Estado desconocido: " + actual);
        }
    }

    public void eliminar(Long id) {
        log.info("Eliminando cotizacion con id: {}", id);
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotizacion no encontrada con id: " + id));

        if (cotizacion.getEstado() == EstadoCotizacion.APROBADA) {
            throw new RuntimeException(
                "No se puede eliminar una cotizacion en estado APROBADA.");
        }

        cotizacionRepository.deleteById(id);
        log.info("Cotizacion eliminada con id: {}", id);
    }

    private void verificarClienteExiste(Long clienteId) {
        try {
            log.info("Verificando cliente id: {} en ms-cliente", clienteId);
            webClientCliente.get()
                    .uri("/api/clientes/{id}", clienteId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Cliente id: {} no encontrado", clienteId);
            throw new ResourceNotFoundException("El cliente con id " + clienteId + " no existe");
        }
    }

    private CotizacionDTO convertirADTO(Cotizacion cotizacion) {
        CotizacionDTO dto = new CotizacionDTO();
        dto.setId(cotizacion.getId());
        dto.setProyectoId(cotizacion.getProyectoId());
        dto.setClienteId(cotizacion.getClienteId());
        dto.setFechaCreacion(cotizacion.getFechaCreacion());
        dto.setEstado(cotizacion.getEstado());
        dto.setPrecioTotalMateriales(cotizacion.getPrecioTotalMateriales());
        dto.setPrecioTotalServicios(cotizacion.getPrecioTotalServicios());
        dto.setPrecioTotal(cotizacion.getPrecioTotal());

        if (cotizacion.getDetalles() != null) {
            dto.setDetalles(cotizacion.getDetalles().stream()
                    .map(this::convertirDetalleADTO)
                    .toList());
        }
        return dto;
    }

    private DetalleCotizacionDTO convertirDetalleADTO(DetalleCotizacion detalle) {
        DetalleCotizacionDTO dto = new DetalleCotizacionDTO();
        dto.setId(detalle.getId());
        dto.setTipo(detalle.getTipo());
        dto.setReferenciaId(detalle.getReferenciaId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        dto.setDescripcion(detalle.getDescripcion());
        dto.setCotizacionId(detalle.getCotizacion().getId());
        return dto;
    }
}
