package com.constructapp.ms_proveedor_material.service;

import com.constructapp.ms_proveedor_material.exception.ResourceNotFoundException;

import com.constructapp.ms_proveedor_material.dto.PrecioProveedorDTO;
import com.constructapp.ms_proveedor_material.model.PrecioProveedor;
import com.constructapp.ms_proveedor_material.model.ProveedorMaterial;
import com.constructapp.ms_proveedor_material.repository.PrecioProveedorRepository;
import com.constructapp.ms_proveedor_material.repository.ProveedorMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrecioProveedorService {

    private final PrecioProveedorRepository precioRepository;

    private final ProveedorMaterialRepository proveedorRepository;

    private final WebClient webClient;

    public List<PrecioProveedorDTO> listarPorProveedor(Long proveedorId) {
        log.info("Listando precios del proveedor id: {}", proveedorId);

        if (!proveedorRepository.existsById(proveedorId)) {
            throw new ResourceNotFoundException(
                    "Proveedor no encontrado con id: " + proveedorId);
        }

        return precioRepository.findByProveedorId(proveedorId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<PrecioProveedorDTO> listarPorMaterial(Long materialId) {
        log.info("Listando precios para material id: {}", materialId);
        return precioRepository.findByMaterialId(materialId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public PrecioProveedorDTO crear(PrecioProveedorDTO dto) {
        log.info("Creando precio para material id: {} del proveedor id: {}",
                dto.getMaterialId(), dto.getProveedorId());

        ProveedorMaterial proveedor = proveedorRepository
                .findById(dto.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con id: " + dto.getProveedorId()));

        verificarMaterialExiste(dto.getMaterialId());

        if (precioRepository.existsByProveedorIdAndMaterialId(
                dto.getProveedorId(), dto.getMaterialId())) {
            throw new RuntimeException(
                    "Este proveedor ya tiene un precio registrado " +
                            "para el material id: " + dto.getMaterialId());
        }

        PrecioProveedor precio = convertirAEntidad(dto, proveedor);
        PrecioProveedor guardado = precioRepository.save(precio);
        log.info("Precio creado con id: {}", guardado.getId());
        return convertirADTO(guardado);
    }

    public PrecioProveedorDTO actualizar(Long id, PrecioProveedorDTO dto) {
        log.info("Actualizando precio con id: {}", id);

        PrecioProveedor precio = precioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Precio no encontrado con id: " + id));

        precio.setPrecio(dto.getPrecio());
        precio.setStockDisponible(dto.getStockDisponible());

        PrecioProveedor actualizado = precioRepository.save(precio);
        log.info("Precio actualizado con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando precio con id: {}", id);
        if (!precioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Precio no encontrado con id: " + id);
        }
        precioRepository.deleteById(id);
        log.info("Precio eliminado con id: {}", id);
    }

    private void verificarMaterialExiste(Long materialId) {
        try {
            log.info("Verificando existencia de material id: {} en ms-catalogo",
                    materialId);

            webClient.get()
                    .uri("/api/materiales/{id}", materialId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.info("Material id: {} verificado exitosamente", materialId);

        } catch (Exception e) {
            log.error("Material id: {} no encontrado en ms-catalogo", materialId);
            throw new ResourceNotFoundException(
                    "El material con id " + materialId +
                            " no existe en el catálogo");
        }
    }

    private PrecioProveedorDTO convertirADTO(PrecioProveedor precio) {
        PrecioProveedorDTO dto = new PrecioProveedorDTO();
        dto.setId(precio.getId());
        dto.setMaterialId(precio.getMaterialId());
        dto.setPrecio(precio.getPrecio());
        dto.setStockDisponible(precio.getStockDisponible());
        dto.setProveedorId(precio.getProveedor().getId());
        dto.setNombreProveedor(precio.getProveedor().getNombre());
        return dto;
    }

    private PrecioProveedor convertirAEntidad(PrecioProveedorDTO dto,
            ProveedorMaterial proveedor) {
        PrecioProveedor precio = new PrecioProveedor();
        precio.setMaterialId(dto.getMaterialId());
        precio.setPrecio(dto.getPrecio());
        precio.setStockDisponible(dto.getStockDisponible());
        // Asigna el objeto proveedor completo, no solo el id
        precio.setProveedor(proveedor);
        return precio;
    }
}