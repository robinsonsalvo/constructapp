package com.constructapp.ms_resena.service;

import com.constructapp.ms_resena.dto.ResenaDTO;
import com.constructapp.ms_resena.model.Resena;
import com.constructapp.ms_resena.repository.ResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Reseñas")
class ResenaServiceTest {

    @Mock private ResenaRepository resenaRepository;
    @Mock private WebClient webClientCliente;
    @Mock private WebClient webClientProveedorServicio;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private ResenaService resenaService;

    @BeforeEach
    void setUp() {
        resenaService = new ResenaService(resenaRepository, webClientCliente, webClientProveedorServicio);
    }

    private void mockWebClientClienteOk() {
        when(webClientCliente.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("id", 1)));
    }

    private void mockWebClientProveedorOk() {
        WebClient.RequestHeadersUriSpec provSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec provHeaders = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec provResponse = mock(WebClient.ResponseSpec.class);
        when(webClientProveedorServicio.get()).thenReturn(provSpec);
        when(provSpec.uri(anyString(), any(Object[].class))).thenReturn(provHeaders);
        when(provHeaders.retrieve()).thenReturn(provResponse);
        when(provResponse.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("id", 1)));
    }

    private Resena crearResenaEntidad(Long id, Long clienteId, Long proveedorId, int puntuacion) {
        Resena r = new Resena();
        r.setId(id);
        r.setClienteId(clienteId);
        r.setProveedorServicioId(proveedorId);
        r.setPuntuacion(puntuacion);
        r.setComentario("Buen trabajo");
        r.setFechaResena(LocalDate.now());
        return r;
    }

    private ResenaDTO crearResenaDTO(Long clienteId, Long proveedorId, int puntuacion) {
        ResenaDTO dto = new ResenaDTO();
        dto.setClienteId(clienteId);
        dto.setProveedorServicioId(proveedorId);
        dto.setPuntuacion(puntuacion);
        dto.setComentario("Buen trabajo");
        return dto;
    }

    @Test
    @DisplayName("listarTodas - retorna lista con todas las reseñas")
    void listarTodas_retornaLista() {
        // Given
        List<Resena> resenas = List.of(
            crearResenaEntidad(1L, 1L, 1L, 5),
            crearResenaEntidad(2L, 2L, 1L, 4)
        );
        when(resenaRepository.findAll()).thenReturn(resenas);

        // When
        List<ResenaDTO> resultado = resenaService.listarTodas();

        // Then
        assertEquals(2, resultado.size());
        verify(resenaRepository).findAll();
    }

    @Test
    @DisplayName("listarTodas - retorna lista vacía cuando no hay reseñas")
    void listarTodas_listaVacia() {
        // Given
        when(resenaRepository.findAll()).thenReturn(List.of());

        // When
        List<ResenaDTO> resultado = resenaService.listarTodas();

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("obtenerPorId - retorna reseña cuando existe")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        Resena resena = crearResenaEntidad(1L, 1L, 1L, 5);
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        // When
        ResenaDTO resultado = resenaService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(5, resultado.getPuntuacion());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción cuando no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> resenaService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("crear - crea reseña correctamente cuando datos son válidos")
    void crear_datosValidos_creaResena() {
        // Given
        mockWebClientClienteOk();
        mockWebClientProveedorOk();
        ResenaDTO dto = crearResenaDTO(1L, 1L, 5);
        when(resenaRepository.findByClienteId(1L)).thenReturn(List.of());
        Resena guardada = crearResenaEntidad(1L, 1L, 1L, 5);
        when(resenaRepository.save(any(Resena.class))).thenReturn(guardada);

        // When
        ResenaDTO resultado = resenaService.crear(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(5, resultado.getPuntuacion());
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    @DisplayName("crear - lanza excepción si cliente ya reseñó al mismo proveedor")
    void crear_resenaDuplicada_lanzaExcepcion() {
        // Given
        mockWebClientClienteOk();
        mockWebClientProveedorOk();
        ResenaDTO dto = crearResenaDTO(1L, 1L, 4);
        Resena existente = crearResenaEntidad(1L, 1L, 1L, 5);
        when(resenaRepository.findByClienteId(1L)).thenReturn(List.of(existente));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> resenaService.crear(dto));
        assertTrue(ex.getMessage().contains("ya tiene una reseña"));
    }

    @Test
    @DisplayName("crear - lanza excepción si cliente no existe")
    void crear_clienteNoExiste_lanzaExcepcion() {
        // Given
        when(webClientCliente.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.error(new RuntimeException("404")));
        ResenaDTO dto = crearResenaDTO(99L, 1L, 5);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> resenaService.crear(dto));
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    @DisplayName("listarPorProveedor - retorna reseñas del proveedor indicado")
    void listarPorProveedor_retornaLista() {
        // Given
        List<Resena> resenas = List.of(
            crearResenaEntidad(1L, 1L, 5L, 5),
            crearResenaEntidad(2L, 2L, 5L, 3)
        );
        when(resenaRepository.findByProveedorServicioId(5L)).thenReturn(resenas);

        // When
        List<ResenaDTO> resultado = resenaService.listarPorProveedor(5L);

        // Then
        assertEquals(2, resultado.size());
        resultado.forEach(r -> assertEquals(5L, r.getProveedorServicioId()));
    }

    @Test
    @DisplayName("eliminar - elimina reseña existente correctamente")
    void eliminar_existente_eliminaOk() {
        // Given
        when(resenaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(resenaRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> resenaService.eliminar(1L));
        verify(resenaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si reseña no existe")
    void eliminar_noExistente_lanzaExcepcion() {
        // Given
        when(resenaRepository.existsById(99L)).thenReturn(false);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> resenaService.eliminar(99L));
        assertTrue(ex.getMessage().contains("99"));
    }
}
