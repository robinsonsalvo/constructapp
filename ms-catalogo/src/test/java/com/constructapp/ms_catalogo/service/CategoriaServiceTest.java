package com.constructapp.ms_catalogo.service;

import com.constructapp.ms_catalogo.dto.CategoriaDTO;
import com.constructapp.ms_catalogo.model.Categoria;
import com.constructapp.ms_catalogo.repository.CategoriaRepository;
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
@DisplayName("Tests del servicio de Categorías")
class CategoriaServiceTest {

    @Mock private CategoriaRepository categoriaRepository;
    @InjectMocks private CategoriaService categoriaService;

    private Categoria crearCategoria(Long id, String nombre) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre(nombre);
        c.setDescripcion("Descripción de " + nombre);
        return c;
    }

    private CategoriaDTO crearCategoriaDTO(String nombre) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre(nombre);
        dto.setDescripcion("Descripción de " + nombre);
        return dto;
    }

    @Test
    @DisplayName("listarTodas - retorna todas las categorías")
    void listarTodas_retornaLista() {
        // Given
        when(categoriaRepository.findAll()).thenReturn(List.of(
            crearCategoria(1L, "Cemento"),
            crearCategoria(2L, "Fierro")
        ));

        // When
        List<CategoriaDTO> resultado = categoriaService.listarTodas();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("obtenerPorId - retorna categoría existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(crearCategoria(1L, "Cemento")));

        // When
        CategoriaDTO resultado = categoriaService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Cemento", resultado.getNombre());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción cuando no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> categoriaService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("crear - crea categoría correctamente")
    void crear_datosValidos_creaCategoria() {
        // Given
        when(categoriaRepository.existsByNombre("Cemento")).thenReturn(false);
        Categoria guardada = crearCategoria(1L, "Cemento");
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(guardada);

        // When
        CategoriaDTO resultado = categoriaService.crear(crearCategoriaDTO("Cemento"));

        // Then
        assertNotNull(resultado);
        assertEquals("Cemento", resultado.getNombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("crear - lanza excepción si nombre ya existe")
    void crear_nombreDuplicado_lanzaExcepcion() {
        // Given
        when(categoriaRepository.existsByNombre("Cemento")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> categoriaService.crear(crearCategoriaDTO("Cemento")));
        assertTrue(ex.getMessage().contains("Cemento"));
    }

    @Test
    @DisplayName("actualizar - actualiza categoría existente")
    void actualizar_existente_actualizaOk() {
        // Given
        Categoria existente = crearCategoria(1L, "Cemento");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(existente);

        // When
        CategoriaDTO resultado = categoriaService.actualizar(1L, crearCategoriaDTO("Cemento Premium"));

        // Then
        assertNotNull(resultado);
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("eliminar - elimina categoría existente")
    void eliminar_existente_eliminaOk() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> categoriaService.eliminar(1L));
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si no existe")
    void eliminar_noExistente_lanzaExcepcion() {
        // Given
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> categoriaService.eliminar(99L));
        assertTrue(ex.getMessage().contains("99"));
    }
}
