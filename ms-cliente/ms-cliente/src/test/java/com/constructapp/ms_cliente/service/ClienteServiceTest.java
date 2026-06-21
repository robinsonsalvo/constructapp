package com.constructapp.ms_cliente.service;

import com.constructapp.ms_cliente.dto.ClienteDTO;
import com.constructapp.ms_cliente.model.Cliente;
import com.constructapp.ms_cliente.model.TipoCliente;
import com.constructapp.ms_cliente.repository.ClienteRepository;
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
@DisplayName("Tests del servicio de Clientes")
class ClienteServiceTest {

    @Mock private ClienteRepository clienteRepository;
    @InjectMocks private ClienteService clienteService;

    private Cliente crearCliente(Long id, String email, String rut) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setNombre("Juan");
        c.setApellido("Pérez");
        c.setEmail(email);
        c.setTelefono("987654321");
        c.setTipo(TipoCliente.PARTICULAR);
        c.setRut(rut);
        c.setDireccion("Av. Test 123");
        return c;
    }

    private ClienteDTO crearDTO(String email, String rut) {
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setEmail(email);
        dto.setTelefono("987654321");
        dto.setTipo(TipoCliente.PARTICULAR);
        dto.setRut(rut);
        dto.setDireccion("Av. Test 123");
        return dto;
    }

    @Test
    @DisplayName("listarTodos - retorna todos los clientes")
    void listarTodos_retornaLista() {
        // Given
        when(clienteRepository.findAll()).thenReturn(List.of(
            crearCliente(1L, "a@a.com", "11111111-1"),
            crearCliente(2L, "b@b.com", "22222222-2")
        ));

        // When
        List<ClienteDTO> resultado = clienteService.listarTodos();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("listarTodos - retorna lista vacía si no hay clientes")
    void listarTodos_listaVacia() {
        // Given
        when(clienteRepository.findAll()).thenReturn(List.of());

        // When
        List<ClienteDTO> resultado = clienteService.listarTodos();

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("obtenerPorId - retorna cliente existente")
    void obtenerPorId_existente_retornaDTO() {
        // Given
        when(clienteRepository.findById(1L))
            .thenReturn(Optional.of(crearCliente(1L, "a@a.com", "11111111-1")));

        // When
        ClienteDTO resultado = clienteService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("a@a.com", resultado.getEmail());
        assertEquals("Juan", resultado.getNombre());
    }

    @Test
    @DisplayName("obtenerPorId - lanza excepción si no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> clienteService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("crear - crea cliente correctamente")
    void crear_datosValidos_creaCliente() {
        // Given
        when(clienteRepository.existsByEmail("a@a.com")).thenReturn(false);
        when(clienteRepository.existsByRut("11111111-1")).thenReturn(false);
        when(clienteRepository.save(any())).thenReturn(crearCliente(1L, "a@a.com", "11111111-1"));

        // When
        ClienteDTO resultado = clienteService.crear(crearDTO("a@a.com", "11111111-1"));

        // Then
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(clienteRepository).save(any());
    }

    @Test
    @DisplayName("crear - lanza excepción si email ya existe")
    void crear_emailDuplicado_lanzaExcepcion() {
        // Given
        when(clienteRepository.existsByEmail("a@a.com")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> clienteService.crear(crearDTO("a@a.com", "11111111-1")));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    @DisplayName("crear - lanza excepción si RUT ya existe")
    void crear_rutDuplicado_lanzaExcepcion() {
        // Given
        when(clienteRepository.existsByEmail("a@a.com")).thenReturn(false);
        when(clienteRepository.existsByRut("11111111-1")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> clienteService.crear(crearDTO("a@a.com", "11111111-1")));
        assertTrue(ex.getMessage().contains("RUT"));
    }

    @Test
    @DisplayName("actualizar - actualiza cliente correctamente")
    void actualizar_datosValidos_actualizaOk() {
        // Given
        Cliente existente = crearCliente(1L, "a@a.com", "11111111-1");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any())).thenReturn(existente);

        // When & Then
        assertDoesNotThrow(() -> clienteService.actualizar(1L, crearDTO("a@a.com", "11111111-1")));
        verify(clienteRepository).save(any());
    }

    @Test
    @DisplayName("actualizar - lanza excepción si nuevo email ya lo usa otro cliente")
    void actualizar_emailYaUsadoPorOtro_lanzaExcepcion() {
        // Given
        Cliente existente = crearCliente(1L, "a@a.com", "11111111-1");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.existsByEmail("nuevo@nuevo.com")).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> clienteService.actualizar(1L, crearDTO("nuevo@nuevo.com", "11111111-1")));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    @DisplayName("eliminar - elimina cliente existente")
    void eliminar_existente_eliminaOk() {
        // Given
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> clienteService.eliminar(1L));
        verify(clienteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - lanza excepción si no existe")
    void eliminar_noExistente_lanzaExcepcion() {
        // Given
        when(clienteRepository.existsById(99L)).thenReturn(false);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> clienteService.eliminar(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("listarPorTipo - retorna clientes del tipo indicado")
    void listarPorTipo_retornaFiltrado() {
        // Given
        when(clienteRepository.findByTipo(TipoCliente.PARTICULAR)).thenReturn(List.of(
            crearCliente(1L, "a@a.com", "11111111-1")
        ));

        // When
        List<ClienteDTO> resultado = clienteService.listarPorTipo(TipoCliente.PARTICULAR);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(TipoCliente.PARTICULAR, resultado.get(0).getTipo());
    }
}
