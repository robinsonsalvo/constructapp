package com.constructapp.ms_proveedor_servicio.service;

import com.constructapp.ms_proveedor_servicio.dto.ProveedorServicioDTO;
import com.constructapp.ms_proveedor_servicio.model.ProveedorServicio;
import com.constructapp.ms_proveedor_servicio.model.TipoServicio;
import com.constructapp.ms_proveedor_servicio.repository.ProveedorServicioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.constructapp.ms_proveedor_servicio.model.Modalidad;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Proveedores de Servicio")
class ProveedorServicioServiceTest {

    @Mock private ProveedorServicioRepository proveedorServicioRepository;
    @InjectMocks private ProveedorServicioService proveedorServicioService;

    private ProveedorServicio crearProveedor(Long id, String rut) {
        ProveedorServicio p = new ProveedorServicio();
        p.setId(id);
        p.setNombre("Carlos");
        p.setApellido("González");
        p.setRut(rut);
        p.setTipoServicio(TipoServicio.ELECTRICIDAD);
        p.setDescripcion("Electricista certificado");
        p.setPrecio(50000.0);
        p.setModalidad(Modalidad.POR_PROYECTO);
        p.setRegion("Metropolitana");
        p.setDisponible(true);
        return p;
    }

    private ProveedorServicioDTO crearDTO(String rut) {
        ProveedorServicioDTO dto = new ProveedorServicioDTO();
        dto.setNombre("Carlos");
        dto.setApellido("González");
        dto.setRut(rut);
        dto.setTipoServicio(TipoServicio.ELECTRICIDAD);
        dto.setDescripcion("Electricista certificado");
        dto.setPrecio(50000.0);
        dto.setModalidad(Modalidad.POR_PROYECTO);
        dto.setRegion("Metropolitana");
        dto.setDisponible(true);
        return dto;
    }

    @Test
    @DisplayName("listarTodos - retorna todos los proveedores de servicio")
    void listarTodos_retornaLista() {
        // Given
        when(proveedorServicioRepository.findAll()).thenReturn(List.of(
            crearProveedor(1L, "11111111-1"),
            crearProveedor(2L, "22222222-2")
        ));

        // When
        List<ProveedorServicioDTO> resultado = proveedorServicioService.listarTodos();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("obtenerPorId - retorna proveedor existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(proveedorServicioRepository.findById(1L))
            .thenReturn(Optional.of(crearProveedor(1L, "11111111-1")));

        // When
        ProveedorServicioDTO resultado = proveedorServicioService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getNombre());
        assertEquals(TipoServicio.ELECTRICIDAD, resultado.getTipoServicio());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción si no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(proveedorServicioRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> proveedorServicioService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("crear - crea proveedor correctamente")
    void crear_datosValidos_creaProveedor() {
        // Given
        when(proveedorServicioRepository.existsByRut("11111111-1")).thenReturn(false);
        when(proveedorServicioRepository.save(any())).thenReturn(crearProveedor(1L, "11111111-1"));

        // When
        ProveedorServicioDTO resultado = proveedorServicioService.crear(crearDTO("11111111-1"));

        // Then
        assertNotNull(resultado);
        verify(proveedorServicioRepository).save(any());
    }

    @Test
    @DisplayName("crear - lanza excepción si RUT ya existe")
    void crear_rutDuplicado_lanzaExcepcion() {
        // Given
        when(proveedorServicioRepository.existsByRut("11111111-1")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> proveedorServicioService.crear(crearDTO("11111111-1")));
        assertTrue(ex.getMessage().contains("RUT"));
    }

    @Test
    @DisplayName("actualizar - actualiza proveedor sin cambiar RUT")
    void actualizar_sinCambiarRut_ok() {
        // Given
        ProveedorServicio existente = crearProveedor(1L, "11111111-1");
        when(proveedorServicioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorServicioRepository.save(any())).thenReturn(existente);

        // When & Then
        assertDoesNotThrow(() -> proveedorServicioService.actualizar(1L, crearDTO("11111111-1")));
        verify(proveedorServicioRepository).save(any());
    }

    @Test
    @DisplayName("actualizar - lanza excepción si nuevo RUT ya lo usa otro proveedor")
    void actualizar_rutYaUsadoPorOtro_lanzaExcepcion() {
        // Given
        ProveedorServicio existente = crearProveedor(1L, "11111111-1");
        when(proveedorServicioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorServicioRepository.existsByRut("33333333-3")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> proveedorServicioService.actualizar(1L, crearDTO("33333333-3")));
        assertTrue(ex.getMessage().contains("RUT"));
    }

    @Test
    @DisplayName("listarDisponibles - retorna solo proveedores disponibles")
    void listarDisponibles_soloDisponibles() {
        // Given
        when(proveedorServicioRepository.findByDisponible(true)).thenReturn(List.of(
            crearProveedor(1L, "11111111-1")
        ));

        // When
        List<ProveedorServicioDTO> resultado = proveedorServicioService.listarDisponibles();

        // Then
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDisponible());
    }

    @Test
    @DisplayName("listarPorTipo - retorna proveedores del tipo indicado")
    void listarPorTipo_retornaFiltrado() {
        // Given
        when(proveedorServicioRepository.findByTipoServicio(TipoServicio.ELECTRICIDAD)).thenReturn(List.of(
            crearProveedor(1L, "11111111-1")
        ));

        // When
        List<ProveedorServicioDTO> resultado = proveedorServicioService.listarPorTipo(TipoServicio.ELECTRICIDAD);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(TipoServicio.ELECTRICIDAD, resultado.get(0).getTipoServicio());
    }

    @Test
    @DisplayName("eliminar - elimina proveedor existente")
    void eliminar_existente_eliminaOk() {
        // Given
        when(proveedorServicioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(proveedorServicioRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> proveedorServicioService.eliminar(1L));
        verify(proveedorServicioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si no existe")
    void eliminar_noExistente_lanzaExcepcion() {
        // Given
        when(proveedorServicioRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> proveedorServicioService.eliminar(99L));
    }
}
