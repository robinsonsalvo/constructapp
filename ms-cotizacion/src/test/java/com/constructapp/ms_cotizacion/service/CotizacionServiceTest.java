package com.constructapp.ms_cotizacion.service;

import com.constructapp.ms_cotizacion.dto.CotizacionDTO;
import com.constructapp.ms_cotizacion.model.Cotizacion;
import com.constructapp.ms_cotizacion.model.EstadoCotizacion;
import com.constructapp.ms_cotizacion.repository.CotizacionRepository;
import com.constructapp.ms_cotizacion.repository.DetalleCotizacionRepository;
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
@DisplayName("Tests del servicio de Cotizaciones")
class CotizacionServiceTest {

    @Mock private CotizacionRepository cotizacionRepository;
    @Mock private DetalleCotizacionRepository detalleRepository;
    @Mock private WebClient webClientCliente;
    @Mock private WebClient webClientProveedorMaterial;
    @Mock private WebClient webClientProveedorServicio;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private CotizacionService cotizacionService;

    @BeforeEach
    void setUp() {
        cotizacionService = new CotizacionService(
            cotizacionRepository, detalleRepository,
            webClientCliente, webClientProveedorMaterial, webClientProveedorServicio);
    }

    private Cotizacion crearCotizacion(Long id, EstadoCotizacion estado) {
        Cotizacion c = new Cotizacion();
        c.setId(id);
        c.setClienteId(1L);
        c.setProyectoId(1L);
        c.setEstado(estado);
        c.setFechaCreacion(LocalDate.now());
        c.setPrecioTotal(1000.0);
        c.setPrecioTotalMateriales(600.0);
        c.setPrecioTotalServicios(400.0);
        c.setDetalles(List.of());
        return c;
    }

    @Test
    @DisplayName("listarTodas - retorna todas las cotizaciones")
    void listarTodas_retornaLista() {
        // Given
        when(cotizacionRepository.findAll()).thenReturn(List.of(
            crearCotizacion(1L, EstadoCotizacion.BORRADOR),
            crearCotizacion(2L, EstadoCotizacion.ENVIADA)
        ));

        // When
        List<CotizacionDTO> resultado = cotizacionService.listarTodas();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("obtenerPorId - retorna cotización existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(cotizacionRepository.findById(1L))
            .thenReturn(Optional.of(crearCotizacion(1L, EstadoCotizacion.BORRADOR)));

        // When
        CotizacionDTO resultado = cotizacionService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(EstadoCotizacion.BORRADOR, resultado.getEstado());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción si no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(cotizacionRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> cotizacionService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("cambiarEstado - BORRADOR a ENVIADA es válido")
    void cambiarEstado_borradorAEnviada_ok() {
        // Given
        Cotizacion c = crearCotizacion(1L, EstadoCotizacion.BORRADOR);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(c));
        when(cotizacionRepository.save(any())).thenReturn(c);

        // When & Then
        assertDoesNotThrow(() -> cotizacionService.cambiarEstado(1L, EstadoCotizacion.ENVIADA));
        verify(cotizacionRepository).save(any());
    }

    @Test
    @DisplayName("cambiarEstado - ENVIADA a APROBADA es válido")
    void cambiarEstado_enviadaAAprobada_ok() {
        // Given
        Cotizacion c = crearCotizacion(1L, EstadoCotizacion.ENVIADA);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(c));
        when(cotizacionRepository.save(any())).thenReturn(c);

        // When & Then
        assertDoesNotThrow(() -> cotizacionService.cambiarEstado(1L, EstadoCotizacion.APROBADA));
        verify(cotizacionRepository).save(any());
    }

    @Test
    @DisplayName("cambiarEstado - ENVIADA a RECHAZADA es válido")
    void cambiarEstado_enviadaARechazada_ok() {
        // Given
        Cotizacion c = crearCotizacion(1L, EstadoCotizacion.ENVIADA);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(c));
        when(cotizacionRepository.save(any())).thenReturn(c);

        // When & Then
        assertDoesNotThrow(() -> cotizacionService.cambiarEstado(1L, EstadoCotizacion.RECHAZADA));
        verify(cotizacionRepository).save(any());
    }

    @Test
    @DisplayName("cambiarEstado - BORRADOR a APROBADA lanza excepción")
    void cambiarEstado_borradorAAprobada_lanzaExcepcion() {
        // Given
        Cotizacion c = crearCotizacion(1L, EstadoCotizacion.BORRADOR);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(c));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> cotizacionService.cambiarEstado(1L, EstadoCotizacion.APROBADA));
        assertTrue(ex.getMessage().contains("ENVIADA"));
    }

    @Test
    @DisplayName("cambiarEstado - APROBADA es estado final, lanza excepción")
    void cambiarEstado_aprobadaEsFinal_lanzaExcepcion() {
        // Given
        Cotizacion c = crearCotizacion(1L, EstadoCotizacion.APROBADA);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(c));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> cotizacionService.cambiarEstado(1L, EstadoCotizacion.BORRADOR));
        assertTrue(ex.getMessage().contains("final"));
    }

    @Test
    @DisplayName("cambiarEstado - RECHAZADA es estado final, lanza excepción")
    void cambiarEstado_rechazadaEsFinal_lanzaExcepcion() {
        // Given
        Cotizacion c = crearCotizacion(1L, EstadoCotizacion.RECHAZADA);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(c));

        // When & Then
        assertThrows(RuntimeException.class,
            () -> cotizacionService.cambiarEstado(1L, EstadoCotizacion.ENVIADA));
    }

    @Test
    @DisplayName("eliminar - elimina cotización en estado BORRADOR")
    void eliminar_borrador_eliminaOk() {
        // Given
        Cotizacion c = crearCotizacion(1L, EstadoCotizacion.BORRADOR);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(c));
        doNothing().when(cotizacionRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> cotizacionService.eliminar(1L));
        verify(cotizacionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si cotización está APROBADA")
    void eliminar_aprobada_lanzaExcepcion() {
        // Given
        Cotizacion c = crearCotizacion(1L, EstadoCotizacion.APROBADA);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(c));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> cotizacionService.eliminar(1L));
        assertTrue(ex.getMessage().contains("APROBADA"));
    }
}
