package com.constructapp.ms_proyecto.service;

import com.constructapp.ms_proyecto.dto.ProyectoDTO;
import com.constructapp.ms_proyecto.model.EstadoProyecto;
import com.constructapp.ms_proyecto.model.Proyecto;
import com.constructapp.ms_proyecto.model.TipoProyecto;
import com.constructapp.ms_proyecto.repository.ProyectoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Proyectos")
class ProyectoServiceTest {

    @Mock private ProyectoRepository proyectoRepository;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks private ProyectoService proyectoService;

    private Proyecto crearProyecto(Long id, EstadoProyecto estado) {
        Proyecto p = new Proyecto();
        p.setId(id);
        p.setNombre("Proyecto Test");
        p.setDescripcion("Descripción");
        p.setTipo(TipoProyecto.OBRA_NUEVA);
        p.setEstado(estado);
        p.setClienteId(1L);
        p.setFechaInicio(LocalDate.now());
        p.setFechaEstimadaFin(LocalDate.now().plusMonths(6));
        return p;
    }

    private ProyectoDTO crearProyectoDTO(EstadoProyecto estado) {
        ProyectoDTO dto = new ProyectoDTO();
        dto.setNombre("Proyecto Test");
        dto.setDescripcion("Descripción");
        dto.setTipo(TipoProyecto.OBRA_NUEVA);
        dto.setEstado(estado);
        dto.setClienteId(1L);
        dto.setFechaInicio(LocalDate.now());
        dto.setFechaEstimadaFin(LocalDate.now().plusMonths(6));
        return dto;
    }

    @Test
    @DisplayName("listarTodos - retorna todos los proyectos")
    void listarTodos_retornaLista() {
        // Given
        when(proyectoRepository.findAll()).thenReturn(List.of(
            crearProyecto(1L, EstadoProyecto.COTIZANDO),
            crearProyecto(2L, EstadoProyecto.EN_EJECUCION)
        ));

        // When
        List<ProyectoDTO> resultado = proyectoService.listarTodos();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("obtenerPorId - retorna proyecto existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(proyectoRepository.findById(1L))
            .thenReturn(Optional.of(crearProyecto(1L, EstadoProyecto.COTIZANDO)));

        // When
        ProyectoDTO resultado = proyectoService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(EstadoProyecto.COTIZANDO, resultado.getEstado());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción si no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> proyectoService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("actualizar - COTIZANDO a EN_EJECUCION es válido")
    void actualizar_cotizandoAEnEjecucion_ok() {
        // Given
        Proyecto p = crearProyecto(1L, EstadoProyecto.COTIZANDO);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(proyectoRepository.save(any())).thenReturn(p);

        // When & Then
        assertDoesNotThrow(() -> proyectoService.actualizar(1L, crearProyectoDTO(EstadoProyecto.EN_EJECUCION)));
        verify(proyectoRepository).save(any());
    }

    @Test
    @DisplayName("actualizar - EN_EJECUCION a TERMINADO es válido")
    void actualizar_enEjecucionATerminado_ok() {
        // Given
        Proyecto p = crearProyecto(1L, EstadoProyecto.EN_EJECUCION);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(proyectoRepository.save(any())).thenReturn(p);

        // When & Then
        assertDoesNotThrow(() -> proyectoService.actualizar(1L, crearProyectoDTO(EstadoProyecto.TERMINADO)));
    }

    @Test
    @DisplayName("actualizar - COTIZANDO a TERMINADO lanza excepción")
    void actualizar_cotizandoATerminado_lanzaExcepcion() {
        // Given
        Proyecto p = crearProyecto(1L, EstadoProyecto.COTIZANDO);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(p));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> proyectoService.actualizar(1L, crearProyectoDTO(EstadoProyecto.TERMINADO)));
        assertTrue(ex.getMessage().contains("EN_EJECUCION"));
    }

    @Test
    @DisplayName("actualizar - TERMINADO es estado final, lanza excepción")
    void actualizar_terminadoEsFinal_lanzaExcepcion() {
        // Given
        Proyecto p = crearProyecto(1L, EstadoProyecto.TERMINADO);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(p));

        // When & Then
        assertThrows(RuntimeException.class,
            () -> proyectoService.actualizar(1L, crearProyectoDTO(EstadoProyecto.COTIZANDO)));
    }

    @Test
    @DisplayName("eliminar - elimina proyecto COTIZANDO correctamente")
    void eliminar_cotizando_eliminaOk() {
        // Given
        Proyecto p = crearProyecto(1L, EstadoProyecto.COTIZANDO);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(p));
        doNothing().when(proyectoRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> proyectoService.eliminar(1L));
        verify(proyectoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si proyecto está EN_EJECUCION")
    void eliminar_enEjecucion_lanzaExcepcion() {
        // Given
        Proyecto p = crearProyecto(1L, EstadoProyecto.EN_EJECUCION);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(p));

        // When & Then
        assertThrows(RuntimeException.class, () -> proyectoService.eliminar(1L));
    }

    @Test
    @DisplayName("eliminar - lanza excepción si proyecto está TERMINADO")
    void eliminar_terminado_lanzaExcepcion() {
        // Given
        Proyecto p = crearProyecto(1L, EstadoProyecto.TERMINADO);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(p));

        // When & Then
        assertThrows(RuntimeException.class, () -> proyectoService.eliminar(1L));
    }
}
