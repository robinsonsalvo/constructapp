package com.constructapp.ms_proveedor_material.service;

import com.constructapp.ms_proveedor_material.dto.ProveedorMaterialDTO;
import com.constructapp.ms_proveedor_material.model.ProveedorMaterial;
import com.constructapp.ms_proveedor_material.repository.ProveedorMaterialRepository;
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
@DisplayName("Tests del servicio de Proveedores de Material")
class ProveedorMaterialServiceTest {

    @Mock private ProveedorMaterialRepository proveedorRepository;
    @InjectMocks private ProveedorMaterialService proveedorMaterialService;

    private ProveedorMaterial crearProveedor(Long id, String email, String rut) {
        ProveedorMaterial p = new ProveedorMaterial();
        p.setId(id);
        p.setNombre("Proveedor Test");
        p.setRut(rut);
        p.setEmail(email);
        p.setTelefono("987654321");
        p.setDireccion("Av. Test 123");
        return p;
    }

    private ProveedorMaterialDTO crearDTO(String email, String rut) {
        ProveedorMaterialDTO dto = new ProveedorMaterialDTO();
        dto.setNombre("Proveedor Test");
        dto.setRut(rut);
        dto.setEmail(email);
        dto.setTelefono("987654321");
        dto.setDireccion("Av. Test 123");
        return dto;
    }

    @Test
    @DisplayName("listarTodos - retorna todos los proveedores")
    void listarTodos_retornaLista() {
        // Given
        when(proveedorRepository.findAll()).thenReturn(List.of(
            crearProveedor(1L, "a@a.com", "11111111-1"),
            crearProveedor(2L, "b@b.com", "22222222-2")
        ));

        // When
        List<ProveedorMaterialDTO> resultado = proveedorMaterialService.listarTodos();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("obtenerPorId - retorna proveedor existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(proveedorRepository.findById(1L))
            .thenReturn(Optional.of(crearProveedor(1L, "a@a.com", "11111111-1")));

        // When
        ProveedorMaterialDTO resultado = proveedorMaterialService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("a@a.com", resultado.getEmail());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción si no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> proveedorMaterialService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("crear - crea proveedor correctamente")
    void crear_datosValidos_creaProveedor() {
        // Given
        when(proveedorRepository.existsByEmail("a@a.com")).thenReturn(false);
        when(proveedorRepository.existsByRut("11111111-1")).thenReturn(false);
        when(proveedorRepository.save(any())).thenReturn(crearProveedor(1L, "a@a.com", "11111111-1"));

        // When
        ProveedorMaterialDTO resultado = proveedorMaterialService.crear(crearDTO("a@a.com", "11111111-1"));

        // Then
        assertNotNull(resultado);
        verify(proveedorRepository).save(any());
    }

    @Test
    @DisplayName("crear - lanza excepción si email ya existe")
    void crear_emailDuplicado_lanzaExcepcion() {
        // Given
        when(proveedorRepository.existsByEmail("a@a.com")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> proveedorMaterialService.crear(crearDTO("a@a.com", "11111111-1")));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    @DisplayName("crear - lanza excepción si RUT ya existe")
    void crear_rutDuplicado_lanzaExcepcion() {
        // Given
        when(proveedorRepository.existsByEmail("a@a.com")).thenReturn(false);
        when(proveedorRepository.existsByRut("11111111-1")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> proveedorMaterialService.crear(crearDTO("a@a.com", "11111111-1")));
        assertTrue(ex.getMessage().contains("RUT"));
    }

    @Test
    @DisplayName("actualizar - actualiza proveedor sin cambiar email ni RUT")
    void actualizar_sinCambiarEmailNiRut_ok() {
        // Given
        ProveedorMaterial existente = crearProveedor(1L, "a@a.com", "11111111-1");
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorRepository.save(any())).thenReturn(existente);

        // When & Then
        assertDoesNotThrow(() -> proveedorMaterialService.actualizar(1L, crearDTO("a@a.com", "11111111-1")));
    }

    @Test
    @DisplayName("actualizar - lanza excepción si nuevo email ya lo usa otro proveedor")
    void actualizar_emailYaUsadoPorOtro_lanzaExcepcion() {
        // Given
        ProveedorMaterial existente = crearProveedor(1L, "a@a.com", "11111111-1");
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorRepository.existsByEmail("nuevo@nuevo.com")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> proveedorMaterialService.actualizar(1L, crearDTO("nuevo@nuevo.com", "11111111-1")));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    @DisplayName("eliminar - elimina proveedor existente")
    void eliminar_existente_eliminaOk() {
        // Given
        when(proveedorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(proveedorRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> proveedorMaterialService.eliminar(1L));
        verify(proveedorRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si no existe")
    void eliminar_noExistente_lanzaExcepcion() {
        // Given
        when(proveedorRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> proveedorMaterialService.eliminar(99L));
    }
}
