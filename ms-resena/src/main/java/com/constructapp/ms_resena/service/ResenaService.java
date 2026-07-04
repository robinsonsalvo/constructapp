package com.constructapp.ms_resena.service;

import com.constructapp.ms_resena.exception.ResourceNotFoundException;

import com.constructapp.ms_resena.dto.ResenaDTO;
import com.constructapp.ms_resena.model.Resena;
import com.constructapp.ms_resena.repository.ResenaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final WebClient webClientCliente;
    private final WebClient webClientProveedorServicio;

    public ResenaService(ResenaRepository resenaRepository,
                         @Qualifier("webClientCliente") WebClient webClientCliente,
                         @Qualifier("webClientProveedorServicio") WebClient webClientProveedorServicio) {
        this.resenaRepository = resenaRepository;
        this.webClientCliente = webClientCliente;
        this.webClientProveedorServicio = webClientProveedorServicio;
    }

    public List<ResenaDTO> listarTodas() {
        log.info("Listando todas las resenas");
        return resenaRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ResenaDTO> listarPorProveedor(Long proveedorServicioId) {
        log.info("Listando resenas del proveedor id: {}", proveedorServicioId);
        return resenaRepository.findByProveedorServicioId(proveedorServicioId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ResenaDTO> listarPorCliente(Long clienteId) {
        log.info("Listando resenas del cliente id: {}", clienteId);
        return resenaRepository.findByClienteId(clienteId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public ResenaDTO obtenerPorId(Long id) {
        log.info("Buscando resena con id: {}", id);
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con id: " + id));
        return convertirADTO(resena);
    }

    public ResenaDTO crear(ResenaDTO dto) {
        log.info("Creando resena del cliente id: {} para proveedor id: {}",
                dto.getClienteId(), dto.getProveedorServicioId());

        verificarClienteExiste(dto.getClienteId());
        verificarProveedorExiste(dto.getProveedorServicioId());

        // Un cliente solo puede dejar una reseña por proveedor
        boolean yaReseno = resenaRepository
                .findByClienteId(dto.getClienteId())
                .stream()
                .anyMatch(r -> r.getProveedorServicioId().equals(dto.getProveedorServicioId()));
        if (yaReseno) {
            throw new RuntimeException(
                "El cliente id: " + dto.getClienteId() +
                " ya tiene una reseña registrada para el proveedor id: " +
                dto.getProveedorServicioId());
        }

        Resena resena = convertirAEntidad(dto);
        resena.setFechaResena(LocalDate.now());
        Resena guardada = resenaRepository.save(resena);
        log.info("Resena creada con id: {}", guardada.getId());
        return convertirADTO(guardada);
    }

    public void eliminar(Long id) {
        log.info("Eliminando resena con id: {}", id);
        if (!resenaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resena no encontrada con id: " + id);
        }
        resenaRepository.deleteById(id);
        log.info("Resena eliminada con id: {}", id);
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
            log.error("Cliente id: {} no encontrado en ms-cliente", clienteId);
            throw new ResourceNotFoundException("El cliente con id " + clienteId + " no existe");
        }
    }

    private void verificarProveedorExiste(Long proveedorServicioId) {
        try {
            log.info("Verificando proveedor id: {} en ms-proveedor-servicio", proveedorServicioId);
            webClientProveedorServicio.get()
                    .uri("/api/proveedores-servicio/{id}", proveedorServicioId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Proveedor id: {} no encontrado en ms-proveedor-servicio", proveedorServicioId);
            throw new ResourceNotFoundException("El proveedor con id " + proveedorServicioId + " no existe");
        }
    }

    private ResenaDTO convertirADTO(Resena resena) {
        ResenaDTO dto = new ResenaDTO();
        dto.setId(resena.getId());
        dto.setClienteId(resena.getClienteId());
        dto.setProveedorServicioId(resena.getProveedorServicioId());
        dto.setPuntuacion(resena.getPuntuacion());
        dto.setComentario(resena.getComentario());
        dto.setFechaResena(resena.getFechaResena());
        return dto;
    }

    private Resena convertirAEntidad(ResenaDTO dto) {
        Resena resena = new Resena();
        resena.setClienteId(dto.getClienteId());
        resena.setProveedorServicioId(dto.getProveedorServicioId());
        resena.setPuntuacion(dto.getPuntuacion());
        resena.setComentario(dto.getComentario());
        return resena;
    }
}
