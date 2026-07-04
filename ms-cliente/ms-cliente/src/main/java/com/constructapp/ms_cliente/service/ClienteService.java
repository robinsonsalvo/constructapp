package com.constructapp.ms_cliente.service;

import com.constructapp.ms_cliente.exception.ResourceNotFoundException;


import com.constructapp.ms_cliente.dto.ClienteDTO;
import com.constructapp.ms_cliente.model.Cliente;
import com.constructapp.ms_cliente.model.TipoCliente;
import com.constructapp.ms_cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteDTO> listarTodos() {
        log.info("Listando todos los clientes");
        return clienteRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ClienteDTO> listarPorTipo(TipoCliente tipo) {
        log.info("Listando clientes de tipo: {}", tipo);
        return clienteRepository.findByTipo(tipo).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public ClienteDTO obtenerPorId(Long id) {
        log.info("Buscando cliente con id: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return convertirADTO(cliente);
    }

    public ClienteDTO crear(ClienteDTO dto) {
        log.info("Creando cliente: {} {}", dto.getNombre(), dto.getApellido());
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con el email: " + dto.getEmail());
        }
        if (dto.getRut() != null && !dto.getRut().isBlank() && clienteRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException("Ya existe un cliente con el RUT: " + dto.getRut());
        }
        Cliente cliente = convertirAEntidad(dto);
        Cliente guardado = clienteRepository.save(cliente);
        log.info("Cliente creado con id: {}", guardado.getId());
        return convertirADTO(guardado);
    }

    public ClienteDTO actualizar(Long id, ClienteDTO dto) {
        log.info("Actualizando cliente con id: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        if (!cliente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con el email: " + dto.getEmail());
        }
        if (dto.getRut() != null && !dto.getRut().isBlank()
                && !dto.getRut().equals(cliente.getRut())
                && clienteRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException("Ya existe un cliente con el RUT: " + dto.getRut());
        }
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setTipo(dto.getTipo());
        cliente.setRut(dto.getRut());
        cliente.setDireccion(dto.getDireccion());
        Cliente actualizado = clienteRepository.save(cliente);
        log.info("Cliente actualizado con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando cliente con id: {}", id);
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
        log.info("Cliente eliminado con id: {}", id);
    }

    private ClienteDTO convertirADTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setTipo(cliente.getTipo());
        dto.setRut(cliente.getRut());
        dto.setDireccion(cliente.getDireccion());
        return dto;
    }

    private Cliente convertirAEntidad(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setTipo(dto.getTipo());
        cliente.setRut(dto.getRut());
        cliente.setDireccion(dto.getDireccion());
        return cliente;
    }
        

}
