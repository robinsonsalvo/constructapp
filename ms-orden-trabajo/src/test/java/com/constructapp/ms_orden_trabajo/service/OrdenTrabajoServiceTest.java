package com.constructapp.ms_orden_trabajo.service;

import com.constructapp.ms_orden_trabajo.dto.OrdenTrabajoDTO;
import com.constructapp.ms_orden_trabajo.model.EstadoOrden;
import com.constructapp.ms_orden_trabajo.model.OrdenTrabajo;
import com.constructapp.ms_orden_trabajo.repository.OrdenTrabajoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
@DisplayName("Tests del servicio de Órdenes de Trabajo")
class OrdenTrabajoServiceTest {

    @Mock private OrdenTrabajoRepository ordenRepository;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks private OrdenTrabajoService ordenTrabajoService;

    private OrdenTrabajo crearOrden(Long id, EstadoOrden estado) {
        OrdenTrabajo o = new OrdenTrabajo();
        o.setId(id);
        o.setCotizacionId(1L);
        o.setProyectoId(1L);
        o.setEstado(estado);
        o.setFechaInicio(LocalDate.now());
        return o;
    }

    private void mockWebClientOk() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("id", 1)));
    }

    @Test
    @DisplayName("listarTodas - retorna todas las órdenes")
    void listarTodas_retornaLista() {
        // Given
        when(ordenRepository.findAll()).thenReturn(List.of(
            crearOrden(1L, EstadoOrden.PENDIENTE),
            crearOrden(2L, EstadoOrden.EN_CURSO)
        ));

        // When
        List<OrdenTrabajoDTO> resultado = ordenTrabajoService.listarTodas();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("obtenerPorId - retorna orden existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(ordenRepository.findById(1L))
            .thenReturn(Optional.of(crearOrden(1L, EstadoOrden.PENDIENTE)));

        // When
        OrdenTrabajoDTO resultado = ordenTrabajoService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(EstadoOrden.PENDIENTE, resultado.getEstado());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción si no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> ordenTrabajoService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("crear - crea orden correctamente en estado PENDIENTE")
    void crear_datosValidos_creaOrden() {
        // Given
        mockWebClientOk();
        when(ordenRepository.existsByCotizacionId(1L)).thenReturn(false);
        when(ordenRepository.save(any())).thenReturn(crearOrden(1L, EstadoOrden.PENDIENTE));
        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();
        dto.setCotizacionId(1L);
        dto.setProyectoId(1L);

        // When
        OrdenTrabajoDTO resultado = ordenTrabajoService.crear(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(EstadoOrden.PENDIENTE, resultado.getEstado());
    }

    @Test
    @DisplayName("crear - lanza excepción si ya existe orden para la cotización")
    void crear_cotizacionDuplicada_lanzaExcepcion() {
        // Given
        mockWebClientOk();
        when(ordenRepository.existsByCotizacionId(1L)).thenReturn(true);
        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();
        dto.setCotizacionId(1L);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ordenTrabajoService.crear(dto));
        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    @Test
    @DisplayName("cambiarEstado - PENDIENTE a EN_CURSO es válido")
    void cambiarEstado_pendienteAEnCurso_ok() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any())).thenReturn(orden);

        // When & Then
        assertDoesNotThrow(() -> ordenTrabajoService.cambiarEstado(1L, EstadoOrden.EN_CURSO));
        verify(ordenRepository).save(any());
    }

    @Test
    @DisplayName("cambiarEstado - PENDIENTE a CANCELADA es válido")
    void cambiarEstado_pendienteACancelada_ok() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any())).thenReturn(orden);

        // When & Then
        assertDoesNotThrow(() -> ordenTrabajoService.cambiarEstado(1L, EstadoOrden.CANCELADA));
        verify(ordenRepository).save(any());
    }

    @Test
    @DisplayName("cambiarEstado - EN_CURSO a COMPLETADA es válido")
    void cambiarEstado_enCursoACompletada_ok() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.EN_CURSO);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any())).thenReturn(orden);

        // When & Then
        assertDoesNotThrow(() -> ordenTrabajoService.cambiarEstado(1L, EstadoOrden.COMPLETADA));
        verify(ordenRepository).save(any());
    }

    @Test
    @DisplayName("cambiarEstado - PENDIENTE a COMPLETADA lanza excepción")
    void cambiarEstado_pendienteACompletada_lanzaExcepcion() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ordenTrabajoService.cambiarEstado(1L, EstadoOrden.COMPLETADA));
        assertTrue(ex.getMessage().contains("EN_CURSO") || ex.getMessage().contains("CANCELADA"));
    }

    @Test
    @DisplayName("cambiarEstado - COMPLETADA es estado final, lanza excepción")
    void cambiarEstado_completadaEsFinal_lanzaExcepcion() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.COMPLETADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        // When & Then
        assertThrows(RuntimeException.class,
            () -> ordenTrabajoService.cambiarEstado(1L, EstadoOrden.PENDIENTE));
    }

    @Test
    @DisplayName("cambiarEstado - CANCELADA es estado final, lanza excepción")
    void cambiarEstado_canceladaEsFinal_lanzaExcepcion() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.CANCELADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        // When & Then
        assertThrows(RuntimeException.class,
            () -> ordenTrabajoService.cambiarEstado(1L, EstadoOrden.EN_CURSO));
    }

    @Test
    @DisplayName("eliminar - elimina orden PENDIENTE correctamente")
    void eliminar_pendiente_eliminaOk() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        doNothing().when(ordenRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> ordenTrabajoService.eliminar(1L));
        verify(ordenRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si orden está EN_CURSO")
    void eliminar_enCurso_lanzaExcepcion() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.EN_CURSO);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        // When & Then
        assertThrows(RuntimeException.class, () -> ordenTrabajoService.eliminar(1L));
    }

    @Test
    @DisplayName("eliminar - lanza excepción si orden está COMPLETADA")
    void eliminar_completada_lanzaExcepcion() {
        // Given
        OrdenTrabajo orden = crearOrden(1L, EstadoOrden.COMPLETADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        // When & Then
        assertThrows(RuntimeException.class, () -> ordenTrabajoService.eliminar(1L));
    }
}
