package com.constructapp.ms_comparacion_precios.service;

import com.constructapp.ms_comparacion_precios.dto.ComparacionPrecioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Comparacion de Precios")
@SuppressWarnings("unchecked")
class ComparacionPreciosServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ComparacionPreciosService comparacionPreciosService;

    @BeforeEach
    void setUp() {
        // Given (comun a todos los tests): se simula la cadena fluida de WebClient
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    private ComparacionPrecioDTO crearDTO(Long proveedorId, String nombre, Double precio, Integer stock) {
        ComparacionPrecioDTO dto = new ComparacionPrecioDTO();
        dto.setPrecioProveedorId(proveedorId);
        dto.setMaterialId(1L);
        dto.setNombreProveedor(nombre);
        dto.setPrecio(precio);
        dto.setStockDisponible(stock);
        dto.setProveedorId(proveedorId);
        return dto;
    }

    @Test
    @DisplayName("compararPorMaterial - ordena los precios de menor a mayor")
    void compararPorMaterial_ordenaPorPrecioAscendente() {
        // Given
        List<ComparacionPrecioDTO> precios = List.of(
            crearDTO(1L, "Ferreteria Sur", 5000.0, 10),
            crearDTO(2L, "Construmart", 3000.0, 5),
            crearDTO(3L, "Sodimac", 4000.0, 20)
        );
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.just(precios));

        // When
        List<ComparacionPrecioDTO> resultado = comparacionPreciosService.compararPorMaterial(1L);

        // Then
        assertEquals(3, resultado.size());
        assertEquals(3000.0, resultado.get(0).getPrecio());
        assertEquals(4000.0, resultado.get(1).getPrecio());
        assertEquals(5000.0, resultado.get(2).getPrecio());
        assertEquals("Construmart", resultado.get(0).getNombreProveedor());
    }

    @Test
    @DisplayName("compararPorMaterial - retorna lista vacia si no hay proveedores")
    void compararPorMaterial_sinProveedores_retornaListaVacia() {
        // Given
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.just(List.of()));

        // When
        List<ComparacionPrecioDTO> resultado = comparacionPreciosService.compararPorMaterial(99L);

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("compararPorMaterial - lanza excepcion si el microservicio remoto falla")
    void compararPorMaterial_errorRemoto_lanzaExcepcion() {
        // Given
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new RuntimeException("Servicio no disponible")));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> comparacionPreciosService.compararPorMaterial(1L));
        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    @DisplayName("obtenerMasBarato - retorna el proveedor con el precio mas bajo")
    void obtenerMasBarato_retornaElMasBarato() {
        // Given
        List<ComparacionPrecioDTO> precios = List.of(
            crearDTO(1L, "Ferreteria Sur", 5000.0, 10),
            crearDTO(2L, "Construmart", 3000.0, 5)
        );
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.just(precios));

        // When
        ComparacionPrecioDTO resultado = comparacionPreciosService.obtenerMasBarato(1L);

        // Then
        assertEquals("Construmart", resultado.getNombreProveedor());
        assertEquals(3000.0, resultado.getPrecio());
    }

    @Test
    @DisplayName("obtenerMasBarato - lanza excepcion si no hay proveedores disponibles")
    void obtenerMasBarato_sinProveedores_lanzaExcepcion() {
        // Given
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.just(List.of()));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> comparacionPreciosService.obtenerMasBarato(99L));
        assertTrue(ex.getMessage().contains("99"));
    }
}
