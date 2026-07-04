package com.constructapp.ms_catalogo.service;

import com.constructapp.ms_catalogo.exception.ResourceNotFoundException;

import com.constructapp.ms_catalogo.dto.CategoriaDTO;
import com.constructapp.ms_catalogo.model.Categoria;
import com.constructapp.ms_catalogo.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarTodas() {
        log.info("Listando todas las categorias");
        return categoriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public CategoriaDTO obtenerPorId(Long id) {
        log.info("Buscando categoria con id: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + id));
        return convertirADTO(categoria);
    }

    public CategoriaDTO crear(CategoriaDTO dto) {
        log.info("Creando categoria: {}", dto.getNombre());
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Ya existe una categoria con el nombre: " + dto.getNombre());
        }
        Categoria categoria = convertirAEntidad(dto);
        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoria creada con id: {}", guardada.getId());
        return convertirADTO(guardada);
    }

    public CategoriaDTO actualizar(Long id, CategoriaDTO dto) {
        log.info("Actualizando categoria con id: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + id));
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        Categoria actualizada = categoriaRepository.save(categoria);
        log.info("Categoria actualizada con id: {}", actualizada.getId());
        return convertirADTO(actualizada);
    }

    public void eliminar(Long id) {
        log.info("Eliminando categoria con id: {}", id);
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
        log.info("Categoria eliminada con id: {}", id);
    }

    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }

    private Categoria convertirAEntidad(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }
}