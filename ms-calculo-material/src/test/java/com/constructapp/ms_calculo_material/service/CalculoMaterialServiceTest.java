package com.constructapp.ms_calculo_material.service;

import com.constructapp.ms_calculo_material.dto.CalculoMaterialDTO;
import com.constructapp.ms_calculo_material.model.CalculoMaterial;
import com.constructapp.ms_calculo_material.repository.CalculoMaterialRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Cálculo de Materiales")
class CalculoMaterialServiceTest {

    @Mock private CalculoMaterialRepository calculoRepository;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks private CalculoMaterialService calculoMaterialService;

    private CalculoMaterial crearCalculo(Long id, Long proyectoId, Long materialId) {
        CalculoMaterial c = new CalculoMaterial();
        c.setId(id);
        c.setProyectoId(proyectoId);
        c.setMaterialId(materialId);
        c.setCantidadCalculada(10.0);
        c.setUnidadMedida("kg");
        c.setPrecioEstimado(1000.0);
        c.setObservacion("Test");
        return c;
    }

    private CalculoMaterialDTO crearDTO(Long proyectoId, Long materialId) {
        CalculoMaterialDTO dto = new CalculoMaterialDTO();
        dto.setProyectoId(proyectoId);
        dto.setMaterialId(materialId);
        dto.setCantidadCalculada(10.0);
        dto.setUnidadMedida("kg");
        dto.setObservacion("Test");
        return dto;
    }

    private void mockWebClientMaterial(double precio) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(
            Mono.just(Map.of("nombre", "Cemento", "precioReferencial", precio)));
    }

    @Test
    @DisplayName("listarTodos - retorna todos los cálculos")
    void listarTodos_retornaLista() {
        // Given
        when(calculoRepository.findAll()).thenReturn(List.of(
            crearCalculo(1L, 1L, 1L),
            crearCalculo(2L, 1L, 2L)
        ));

        // When
        List<CalculoMaterialDTO> resultado = calculoMaterialService.listarTodos();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("obtenerPorId - retorna cálculo existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(calculoRepository.findById(1L)).thenReturn(Optional.of(crearCalculo(1L, 1L, 1L)));

        // When
        CalculoMaterialDTO resultado = calculoMaterialService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getProyectoId());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción si no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(calculoRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> calculoMaterialService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("crear - crea cálculo y calcula precio correctamente")
    void crear_datosValidos_creaYCalculaPrecio() {
        // Given
        mockWebClientMaterial(100.0);
        when(calculoRepository.findByProyectoId(1L)).thenReturn(List.of());
        when(calculoRepository.save(any())).thenReturn(crearCalculo(1L, 1L, 1L));

        // When
        CalculoMaterialDTO resultado = calculoMaterialService.crear(crearDTO(1L, 1L));

        // Then
        assertNotNull(resultado);
        verify(calculoRepository).save(any());
    }

    @Test
    @DisplayName("crear - lanza excepción si ya existe cálculo para mismo proyecto y material")
    void crear_duplicado_lanzaExcepcion() {
        // Given
        mockWebClientMaterial(100.0);
        when(calculoRepository.findByProyectoId(1L)).thenReturn(List.of(crearCalculo(1L, 1L, 1L)));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> calculoMaterialService.crear(crearDTO(1L, 1L)));
        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    @Test
    @DisplayName("crear - precio estimado = cantidad × precio referencial")
    void crear_precioCalculadoCorrectamente() {
        // Given
        mockWebClientMaterial(200.0);
        when(calculoRepository.findByProyectoId(1L)).thenReturn(List.of());
        CalculoMaterial guardado = crearCalculo(1L, 1L, 1L);
        guardado.setPrecioEstimado(2000.0);
        when(calculoRepository.save(any())).thenReturn(guardado);

        // When
        CalculoMaterialDTO resultado = calculoMaterialService.crear(crearDTO(1L, 1L));

        // Then
        assertEquals(2000.0, resultado.getPrecioEstimado());
    }

    @Test
    @DisplayName("listarPorProyecto - retorna cálculos del proyecto indicado")
    void listarPorProyecto_retornaLista() {
        // Given
        when(calculoRepository.findByProyectoId(1L)).thenReturn(List.of(
            crearCalculo(1L, 1L, 1L),
            crearCalculo(2L, 1L, 2L)
        ));

        // When
        List<CalculoMaterialDTO> resultado = calculoMaterialService.listarPorProyecto(1L);

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("eliminar - elimina cálculo existente")
    void eliminar_existente_eliminaOk() {
        // Given
        when(calculoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(calculoRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> calculoMaterialService.eliminar(1L));
        verify(calculoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si no existe")
    void eliminar_noExistente_lanzaExcepcion() {
        // Given
        when(calculoRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> calculoMaterialService.eliminar(99L));
    }
}
