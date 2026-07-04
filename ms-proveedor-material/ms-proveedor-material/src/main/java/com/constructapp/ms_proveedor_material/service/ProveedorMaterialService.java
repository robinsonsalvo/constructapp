package com.constructapp.ms_proveedor_material.service;

import com.constructapp.ms_proveedor_material.exception.ResourceNotFoundException;

import com.constructapp.ms_proveedor_material.dto.ProveedorMaterialDTO;
import com.constructapp.ms_proveedor_material.model.ProveedorMaterial;
import com.constructapp.ms_proveedor_material.repository.ProveedorMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProveedorMaterialService {

    private final ProveedorMaterialRepository proveedorRepository;

    public List<ProveedorMaterialDTO> listarTodos() {
        log.info("Listando todos los proveedores de material");
        return proveedorRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public ProveedorMaterialDTO obtenerPorId(Long id) {
        log.info("Buscando proveedor de material con id: {}", id);
        ProveedorMaterial proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con id: " + id));
        return convertirADTO(proveedor);
    }

    public ProveedorMaterialDTO crear(ProveedorMaterialDTO dto) {
        log.info("Creando proveedor de material: {}", dto.getNombre());

        if (proveedorRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException(
                    "Ya existe un proveedor con el email: " + dto.getEmail());
        }

        if (proveedorRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException(
                    "Ya existe un proveedor con el RUT: " + dto.getRut());
        }

        ProveedorMaterial proveedor = convertirAEntidad(dto);
        ProveedorMaterial guardado = proveedorRepository.save(proveedor);
        log.info("Proveedor creado con id: {}", guardado.getId());
        return convertirADTO(guardado);
    }

    public ProveedorMaterialDTO actualizar(Long id, ProveedorMaterialDTO dto) {
        log.info("Actualizando proveedor de material con id: {}", id);

        ProveedorMaterial proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con id: " + id));

        // Validar unicidad de email solo si cambió
        if (!proveedor.getEmail().equalsIgnoreCase(dto.getEmail())
                && proveedorRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException(
                    "Ya existe otro proveedor con el email: " + dto.getEmail());
        }

        // Validar unicidad de RUT solo si cambió
        if (!proveedor.getRut().equals(dto.getRut())
                && proveedorRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException(
                    "Ya existe otro proveedor con el RUT: " + dto.getRut());
        }

        proveedor.setNombre(dto.getNombre());
        proveedor.setRut(dto.getRut());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setRegion(dto.getRegion());

        ProveedorMaterial actualizado = proveedorRepository.save(proveedor);
        log.info("Proveedor actualizado con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando proveedor de material con id: {}", id);

        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Proveedor no encontrado con id: " + id);
        }
        proveedorRepository.deleteById(id);
        log.info("Proveedor eliminado con id: {}", id);
    }

    private ProveedorMaterialDTO convertirADTO(ProveedorMaterial proveedor) {
        ProveedorMaterialDTO dto = new ProveedorMaterialDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setRut(proveedor.getRut());
        dto.setEmail(proveedor.getEmail());
        dto.setTelefono(proveedor.getTelefono());
        dto.setDireccion(proveedor.getDireccion());
        dto.setRegion(proveedor.getRegion());
        return dto;
    }

    private ProveedorMaterial convertirAEntidad(ProveedorMaterialDTO dto) {
        ProveedorMaterial proveedor = new ProveedorMaterial();
        proveedor.setNombre(dto.getNombre());
        proveedor.setRut(dto.getRut());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setRegion(dto.getRegion());
        return proveedor;
    }
}
