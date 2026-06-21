package com.constructapp.ms_proyecto.service;

import com.constructapp.ms_proyecto.dto.ProyectoDTO;
import com.constructapp.ms_proyecto.model.EstadoProyecto;
import com.constructapp.ms_proyecto.model.Proyecto;
import com.constructapp.ms_proyecto.repository.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final WebClient webClient;

    public List<ProyectoDTO> listarTodos() {
        log.info("Listando todos los proyectos");
        return proyectoRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ProyectoDTO> listarPorCliente(Long clienteId) {
        log.info("Listando proyectos del cliente: {}", clienteId);
        return proyectoRepository.findByClienteId(clienteId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ProyectoDTO> listarPorEstado(EstadoProyecto estado) {
        log.info("Listando proyectos con estado: {}", estado);
        return proyectoRepository.findByEstado(estado).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public ProyectoDTO obtenerPorId(Long id) {
        log.info("Buscando proyecto con id: {}", id);
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));
        return convertirADTO(proyecto);
    }

    public ProyectoDTO crear(ProyectoDTO dto) {
        log.info("Creando proyecto para clienteId: {}", dto.getClienteId());
        validarClienteExiste(dto.getClienteId());

        // Fecha de fin estimada debe ser posterior a la fecha de inicio
        if (dto.getFechaEstimadaFin() != null
                && !dto.getFechaEstimadaFin().isAfter(dto.getFechaInicio())) {
            throw new RuntimeException(
                "La fecha estimada de fin debe ser posterior a la fecha de inicio.");
        }

        Proyecto proyecto = convertirAEntidad(dto);
        proyecto.setEstado(EstadoProyecto.COTIZANDO);
        Proyecto guardado = proyectoRepository.save(proyecto);
        log.info("Proyecto creado con id: {}", guardado.getId());
        return convertirADTO(guardado);
    }

    public ProyectoDTO actualizar(Long id, ProyectoDTO dto) {
        log.info("Actualizando proyecto con id: {}", id);
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));

        if (!proyecto.getClienteId().equals(dto.getClienteId())) {
            validarClienteExiste(dto.getClienteId());
        }

        // Validar transición de estado
        validarTransicionEstadoProyecto(proyecto.getEstado(), dto.getEstado());

        // Validar fechas
        if (dto.getFechaEstimadaFin() != null
                && !dto.getFechaEstimadaFin().isAfter(dto.getFechaInicio())) {
            throw new RuntimeException(
                "La fecha estimada de fin debe ser posterior a la fecha de inicio.");
        }

        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setTipo(dto.getTipo());
        proyecto.setEstado(dto.getEstado());
        proyecto.setClienteId(dto.getClienteId());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaEstimadaFin(dto.getFechaEstimadaFin());
        Proyecto actualizado = proyectoRepository.save(proyecto);
        log.info("Proyecto actualizado con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    /**
     * Reglas de transición:
     * COTIZANDO   → EN_EJECUCION
     * EN_EJECUCION → TERMINADO
     * TERMINADO   → estado final, no puede cambiar
     */
    private void validarTransicionEstadoProyecto(EstadoProyecto actual, EstadoProyecto nuevo) {
        if (nuevo == null || actual.equals(nuevo)) return;

        switch (actual) {
            case COTIZANDO:
                if (nuevo != EstadoProyecto.EN_EJECUCION) {
                    throw new RuntimeException(
                        "Un proyecto COTIZANDO solo puede pasar a EN_EJECUCION. Estado solicitado: " + nuevo);
                }
                break;
            case EN_EJECUCION:
                if (nuevo != EstadoProyecto.TERMINADO) {
                    throw new RuntimeException(
                        "Un proyecto EN_EJECUCION solo puede pasar a TERMINADO. Estado solicitado: " + nuevo);
                }
                break;
            case TERMINADO:
                throw new RuntimeException(
                    "Un proyecto TERMINADO no puede cambiar de estado. Es un estado final.");
            default:
                throw new RuntimeException("Estado desconocido: " + actual);
        }
    }

    public void eliminar(Long id) {
        log.info("Eliminando proyecto con id: {}", id);
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));

        if (proyecto.getEstado() == EstadoProyecto.EN_EJECUCION
                || proyecto.getEstado() == EstadoProyecto.TERMINADO) {
            throw new RuntimeException(
                "No se puede eliminar un proyecto en estado " + proyecto.getEstado() +
                ". Solo se pueden eliminar proyectos en estado COTIZANDO.");
        }

        proyectoRepository.deleteById(id);
        log.info("Proyecto eliminado con id: {}", id);
    }

    private void validarClienteExiste(Long clienteId) {
        log.info("Validando cliente con id: {}", clienteId);
        Boolean existe = webClient.get()
                .uri("/api/clientes/" + clienteId)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false)
                .block();
        if (Boolean.FALSE.equals(existe)) {
            throw new RuntimeException("No existe un cliente con id: " + clienteId);
        }
    }

    private ProyectoDTO convertirADTO(Proyecto proyecto) {
        ProyectoDTO dto = new ProyectoDTO();
        dto.setId(proyecto.getId());
        dto.setNombre(proyecto.getNombre());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setTipo(proyecto.getTipo());
        dto.setEstado(proyecto.getEstado());
        dto.setClienteId(proyecto.getClienteId());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setFechaEstimadaFin(proyecto.getFechaEstimadaFin());
        return dto;
    }

    private Proyecto convertirAEntidad(ProyectoDTO dto) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setTipo(dto.getTipo());
        proyecto.setClienteId(dto.getClienteId());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaEstimadaFin(dto.getFechaEstimadaFin());
        return proyecto;
    }
}
