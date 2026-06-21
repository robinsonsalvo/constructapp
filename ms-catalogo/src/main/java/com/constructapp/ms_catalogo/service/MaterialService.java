package com.constructapp.ms_catalogo.service;

import com.constructapp.ms_catalogo.dto.MaterialDTO;
import com.constructapp.ms_catalogo.model.Categoria;
import com.constructapp.ms_catalogo.model.Material;
import com.constructapp.ms_catalogo.repository.CategoriaRepository;
import com.constructapp.ms_catalogo.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CategoriaRepository categoriaRepository;

    public List<MaterialDTO> listarTodos() {
        log.info("Listando todos los materiales");
        return materialRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<MaterialDTO> listarPorCategoria(Long categoriaId) {
        log.info("Listando materiales de categoria id: {}", categoriaId);
        return materialRepository.findByCategoriaId(categoriaId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public MaterialDTO obtenerPorId(Long id) {
        log.info("Buscando material con id: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));
        return convertirADTO(material);
    }

    public MaterialDTO crear(MaterialDTO dto) {
        log.info("Creando material: {}", dto.getNombre());
        if (materialRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Ya existe un material con el nombre: " + dto.getNombre());
        }
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id: " + dto.getCategoriaId()));
        Material material = convertirAEntidad(dto, categoria);
        Material guardado = materialRepository.save(material);
        log.info("Material creado con id: {}", guardado.getId());
        return convertirADTO(guardado);
    }

    public MaterialDTO actualizar(Long id, MaterialDTO dto) {
        log.info("Actualizando material con id: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id: " + dto.getCategoriaId()));
        material.setNombre(dto.getNombre());
        material.setUnidadMedida(dto.getUnidadMedida());
        material.setPrecioReferencial(dto.getPrecioReferencial());
        material.setDescripcion(dto.getDescripcion());
        material.setCategoria(categoria);
        Material actualizado = materialRepository.save(material);
        log.info("Material actualizado con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando material con id: {}", id);
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material no encontrado con id: " + id);
        }
        materialRepository.deleteById(id);
        log.info("Material eliminado con id: {}", id);
    }

    private MaterialDTO convertirADTO(Material material) {
        MaterialDTO dto = new MaterialDTO();
        dto.setId(material.getId());
        dto.setNombre(material.getNombre());
        dto.setUnidadMedida(material.getUnidadMedida());
        dto.setPrecioReferencial(material.getPrecioReferencial());
        dto.setDescripcion(material.getDescripcion());
        dto.setCategoriaId(material.getCategoria().getId());
        return dto;
    }

    private Material convertirAEntidad(MaterialDTO dto, Categoria categoria) {
        Material material = new Material();
        material.setNombre(dto.getNombre());
        material.setUnidadMedida(dto.getUnidadMedida());
        material.setPrecioReferencial(dto.getPrecioReferencial());
        material.setDescripcion(dto.getDescripcion());
        material.setCategoria(categoria);
        return material;
    }
}