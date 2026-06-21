package com.constructapp.ms_catalogo.service;

import com.constructapp.ms_catalogo.dto.MaterialDTO;
import com.constructapp.ms_catalogo.model.Categoria;
import com.constructapp.ms_catalogo.model.Material;
import com.constructapp.ms_catalogo.repository.CategoriaRepository;
import com.constructapp.ms_catalogo.repository.MaterialRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Materiales")
class MaterialServiceTest {

    @Mock private MaterialRepository materialRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @InjectMocks private MaterialService materialService;

    private Categoria crearCategoria(Long id) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre("Categoria Test");
        c.setDescripcion("Desc");
        return c;
    }

    private Material crearMaterial(Long id, String nombre, Long categoriaId) {
        Material m = new Material();
        m.setId(id);
        m.setNombre(nombre);
        m.setUnidadMedida("kg");
        m.setPrecioReferencial(100.0);
        m.setDescripcion("Desc");
        m.setCategoria(crearCategoria(categoriaId));
        return m;
    }

    private MaterialDTO crearMaterialDTO(String nombre, Long categoriaId) {
        MaterialDTO dto = new MaterialDTO();
        dto.setNombre(nombre);
        dto.setUnidadMedida("kg");
        dto.setPrecioReferencial(100.0);
        dto.setDescripcion("Desc");
        dto.setCategoriaId(categoriaId);
        return dto;
    }

    @Test
    @DisplayName("listarTodos - retorna todos los materiales")
    void listarTodos_retornaLista() {
        // Given
        when(materialRepository.findAll()).thenReturn(List.of(
            crearMaterial(1L, "Cemento", 1L),
            crearMaterial(2L, "Arena", 1L)
        ));

        // When
        List<MaterialDTO> resultado = materialService.listarTodos();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("obtenerPorId - retorna material existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(materialRepository.findById(1L)).thenReturn(Optional.of(crearMaterial(1L, "Cemento", 1L)));

        // When
        MaterialDTO resultado = materialService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Cemento", resultado.getNombre());
        assertEquals(100.0, resultado.getPrecioReferencial());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción cuando no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(materialRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> materialService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("crear - crea material correctamente")
    void crear_datosValidos_creaMaterial() {
        // Given
        when(materialRepository.existsByNombre("Cemento")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(crearCategoria(1L)));
        when(materialRepository.save(any(Material.class))).thenReturn(crearMaterial(1L, "Cemento", 1L));

        // When
        MaterialDTO resultado = materialService.crear(crearMaterialDTO("Cemento", 1L));

        // Then
        assertNotNull(resultado);
        assertEquals("Cemento", resultado.getNombre());
    }

    @Test
    @DisplayName("crear - lanza excepción si nombre ya existe")
    void crear_nombreDuplicado_lanzaExcepcion() {
        // Given
        when(materialRepository.existsByNombre("Cemento")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class,
            () -> materialService.crear(crearMaterialDTO("Cemento", 1L)));
    }

    @Test
    @DisplayName("crear - lanza excepción si categoría no existe")
    void crear_categoriaNoExiste_lanzaExcepcion() {
        // Given
        when(materialRepository.existsByNombre("Cemento")).thenReturn(false);
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class,
            () -> materialService.crear(crearMaterialDTO("Cemento", 99L)));
    }

    @Test
    @DisplayName("listarPorCategoria - retorna materiales de la categoría")
    void listarPorCategoria_retornaLista() {
        // Given
        when(materialRepository.findByCategoriaId(1L)).thenReturn(List.of(
            crearMaterial(1L, "Cemento", 1L)
        ));

        // When
        List<MaterialDTO> resultado = materialService.listarPorCategoria(1L);

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("eliminar - lanza excepción si material no existe")
    void eliminar_noExistente_lanzaExcepcion() {
        // Given
        when(materialRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> materialService.eliminar(99L));
    }
}
