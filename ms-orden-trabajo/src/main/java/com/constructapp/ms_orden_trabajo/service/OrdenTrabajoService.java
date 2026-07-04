package com.constructapp.ms_orden_trabajo.service;

import com.constructapp.ms_orden_trabajo.exception.ResourceNotFoundException;

import com.constructapp.ms_orden_trabajo.dto.OrdenTrabajoDTO;
import com.constructapp.ms_orden_trabajo.model.EstadoOrden;
import com.constructapp.ms_orden_trabajo.model.OrdenTrabajo;
import com.constructapp.ms_orden_trabajo.repository.OrdenTrabajoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenRepository;
    private final WebClient webClient;

    public List<OrdenTrabajoDTO> listarTodas() {
        log.info("Listando todas las ordenes de trabajo");
        return ordenRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<OrdenTrabajoDTO> listarPorProyecto(Long proyectoId) {
        log.info("Listando ordenes del proyecto id: {}", proyectoId);
        return ordenRepository.findByProyectoId(proyectoId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<OrdenTrabajoDTO> listarPorEstado(EstadoOrden estado) {
        log.info("Listando ordenes con estado: {}", estado);
        return ordenRepository.findByEstado(estado).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public OrdenTrabajoDTO obtenerPorId(Long id) {
        log.info("Buscando orden de trabajo con id: {}", id);
        OrdenTrabajo orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));
        return convertirADTO(orden);
    }

    public OrdenTrabajoDTO crear(OrdenTrabajoDTO dto) {
        log.info("Creando orden de trabajo para cotizacion id: {}", dto.getCotizacionId());

        verificarCotizacionExiste(dto.getCotizacionId());

        if (ordenRepository.existsByCotizacionId(dto.getCotizacionId())) {
            throw new RuntimeException("Ya existe una orden para la cotizacion id: "
                    + dto.getCotizacionId());
        }

        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setCotizacionId(dto.getCotizacionId());
        orden.setProyectoId(dto.getProyectoId());
        orden.setEstado(EstadoOrden.PENDIENTE);
        orden.setFechaInicio(LocalDate.now());
        orden.setObservaciones(dto.getObservaciones());

        OrdenTrabajo guardada = ordenRepository.save(orden);
        log.info("Orden de trabajo creada con id: {}", guardada.getId());
        return convertirADTO(guardada);
    }

    /**
     * Reglas de transición de estado:
     * PENDIENTE  → EN_CURSO | CANCELADA
     * EN_CURSO   → COMPLETADA | CANCELADA
     * COMPLETADA → no puede cambiar (estado final)
     * CANCELADA  → no puede cambiar (estado final)
     */
    public OrdenTrabajoDTO cambiarEstado(Long id, EstadoOrden nuevoEstado) {
        log.info("Cambiando estado de orden id: {} a {}", id, nuevoEstado);
        OrdenTrabajo orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));

        EstadoOrden estadoActual = orden.getEstado();
        validarTransicionEstado(estadoActual, nuevoEstado);

        orden.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoOrden.COMPLETADA) {
            orden.setFechaFin(LocalDate.now());
        }

        OrdenTrabajo actualizada = ordenRepository.save(orden);
        log.info("Estado de orden id: {} cambiado de {} a {}", id, estadoActual, nuevoEstado);
        return convertirADTO(actualizada);
    }

    private void validarTransicionEstado(EstadoOrden actual, EstadoOrden nuevo) {
        switch (actual) {
            case PENDIENTE:
                if (nuevo != EstadoOrden.EN_CURSO && nuevo != EstadoOrden.CANCELADA) {
                    throw new RuntimeException(
                        "Una orden PENDIENTE solo puede pasar a EN_CURSO o CANCELADA. Estado solicitado: " + nuevo);
                }
                break;
            case EN_CURSO:
                if (nuevo != EstadoOrden.COMPLETADA && nuevo != EstadoOrden.CANCELADA) {
                    throw new RuntimeException(
                        "Una orden EN_CURSO solo puede pasar a COMPLETADA o CANCELADA. Estado solicitado: " + nuevo);
                }
                break;
            case COMPLETADA:
                throw new RuntimeException(
                    "Una orden COMPLETADA no puede cambiar de estado. Es un estado final.");
            case CANCELADA:
                throw new RuntimeException(
                    "Una orden CANCELADA no puede cambiar de estado. Es un estado final.");
            default:
                throw new RuntimeException("Estado desconocido: " + actual);
        }
    }

    public void eliminar(Long id) {
        log.info("Eliminando orden de trabajo con id: {}", id);
        OrdenTrabajo orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));

        if (orden.getEstado() == EstadoOrden.EN_CURSO || orden.getEstado() == EstadoOrden.COMPLETADA) {
            throw new RuntimeException(
                "No se puede eliminar una orden en estado " + orden.getEstado() +
                ". Solo se pueden eliminar órdenes PENDIENTES o CANCELADAS.");
        }

        ordenRepository.deleteById(id);
        log.info("Orden eliminada con id: {}", id);
    }

    private void verificarCotizacionExiste(Long cotizacionId) {
        try {
            log.info("Verificando cotizacion id: {} en ms-cotizacion", cotizacionId);
            webClient.get()
                    .uri("/api/cotizaciones/{id}", cotizacionId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Cotizacion id: {} no encontrada", cotizacionId);
            throw new ResourceNotFoundException("La cotizacion con id " + cotizacionId + " no existe");
        }
    }

    private OrdenTrabajoDTO convertirADTO(OrdenTrabajo orden) {
        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();
        dto.setId(orden.getId());
        dto.setCotizacionId(orden.getCotizacionId());
        dto.setProyectoId(orden.getProyectoId());
        dto.setEstado(orden.getEstado());
        dto.setFechaInicio(orden.getFechaInicio());
        dto.setFechaFin(orden.getFechaFin());
        dto.setObservaciones(orden.getObservaciones());
        return dto;
    }
}
