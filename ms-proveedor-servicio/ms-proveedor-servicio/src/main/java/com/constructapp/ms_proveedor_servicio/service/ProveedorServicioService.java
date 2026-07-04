package com.constructapp.ms_proveedor_servicio.service;

import com.constructapp.ms_proveedor_servicio.exception.ResourceNotFoundException;



import com.constructapp.ms_proveedor_servicio.dto.ProveedorServicioDTO;
import com.constructapp.ms_proveedor_servicio.model.ProveedorServicio;
import com.constructapp.ms_proveedor_servicio.model.TipoServicio;
import com.constructapp.ms_proveedor_servicio.repository.ProveedorServicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProveedorServicioService {

    private final ProveedorServicioRepository proveedorServicioRepository;

    public List<ProveedorServicioDTO> listarTodos() {
        log.info("Listando todos los proveedores de servicio");
        return proveedorServicioRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ProveedorServicioDTO> listarPorTipo(TipoServicio tipoServicio) {
        log.info("Listando proveedores de tipo: {}", tipoServicio);
        return proveedorServicioRepository.findByTipoServicio(tipoServicio).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ProveedorServicioDTO> listarDisponibles() {
        log.info("Listando proveedores disponibles");
        return proveedorServicioRepository.findByDisponible(true).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ProveedorServicioDTO> listarPorRegion(String region) {
        log.info("Listando proveedores de region: {}", region);
        return proveedorServicioRepository.findByRegion(region).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public ProveedorServicioDTO obtenerPorId(Long id) {
        log.info("Buscando proveedor con id: {}", id);
        ProveedorServicio proveedor = proveedorServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        return convertirADTO(proveedor);
    }

    public ProveedorServicioDTO crear(ProveedorServicioDTO dto) {
        log.info("Creando proveedor: {} {}", dto.getNombre(), dto.getApellido());
        if (proveedorServicioRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException("Ya existe un proveedor con el RUT: " + dto.getRut());
        }
        ProveedorServicio proveedor = convertirAEntidad(dto);
        ProveedorServicio guardado = proveedorServicioRepository.save(proveedor);
        log.info("Proveedor creado con id: {}", guardado.getId());
        return convertirADTO(guardado);
    }

    public ProveedorServicioDTO actualizar(Long id, ProveedorServicioDTO dto) {
        log.info("Actualizando proveedor con id: {}", id);
        ProveedorServicio proveedor = proveedorServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        if (!proveedor.getRut().equals(dto.getRut()) && proveedorServicioRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException("Ya existe un proveedor con el RUT: " + dto.getRut());
        }
        proveedor.setNombre(dto.getNombre());
        proveedor.setApellido(dto.getApellido());
        proveedor.setRut(dto.getRut());
        proveedor.setTipoServicio(dto.getTipoServicio());
        proveedor.setDescripcion(dto.getDescripcion());
        proveedor.setPrecio(dto.getPrecio());
        proveedor.setModalidad(dto.getModalidad());
        proveedor.setRegion(dto.getRegion());
        proveedor.setDisponible(dto.getDisponible());
        ProveedorServicio actualizado = proveedorServicioRepository.save(proveedor);
        log.info("Proveedor actualizado con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando proveedor con id: {}", id);
        if (!proveedorServicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con id: " + id);
        }
        proveedorServicioRepository.deleteById(id);
        log.info("Proveedor eliminado con id: {}", id);
    }

    private ProveedorServicioDTO convertirADTO(ProveedorServicio proveedor) {
        ProveedorServicioDTO dto = new ProveedorServicioDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setApellido(proveedor.getApellido());
        dto.setRut(proveedor.getRut());
        dto.setTipoServicio(proveedor.getTipoServicio());
        dto.setDescripcion(proveedor.getDescripcion());
        dto.setPrecio(proveedor.getPrecio());
        dto.setModalidad(proveedor.getModalidad());
        dto.setRegion(proveedor.getRegion());
        dto.setDisponible(proveedor.getDisponible());
        return dto;
    }

    private ProveedorServicio convertirAEntidad(ProveedorServicioDTO dto) {
        ProveedorServicio proveedor = new ProveedorServicio();
        proveedor.setNombre(dto.getNombre());
        proveedor.setApellido(dto.getApellido());
        proveedor.setRut(dto.getRut());
        proveedor.setTipoServicio(dto.getTipoServicio());
        proveedor.setDescripcion(dto.getDescripcion());
        proveedor.setPrecio(dto.getPrecio());
        proveedor.setModalidad(dto.getModalidad());
        proveedor.setRegion(dto.getRegion());
        proveedor.setDisponible(dto.getDisponible());
        return proveedor;
    }
}
