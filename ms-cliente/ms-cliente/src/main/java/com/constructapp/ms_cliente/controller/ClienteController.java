package com.constructapp.ms_cliente.controller;

import com.constructapp.ms_cliente.dto.ClienteDTO;
import com.constructapp.ms_cliente.model.TipoCliente;
import com.constructapp.ms_cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes particulares y empresas")
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Listar todos los clientes")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        log.info("GET /api/clientes");
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @Operation(summary = "Obtener cliente por ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"nombre\":\"Juan\",\"apellido\":\"Perez\",\"email\":\"juan.perez@mail.com\",\"telefono\":\"+56912345678\",\"tipo\":\"PARTICULAR\",\"rut\":\"12345678-9\",\"direccion\":\"Av. Siempre Viva 123\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\":\"Cliente no encontrado con id: 99\"}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerPorId(
            @Parameter(description = "ID del cliente")
            @PathVariable Long id) {

        log.info("GET /api/clientes/{}", id);
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @Operation(summary = "Listar clientes por tipo (PARTICULAR o EMPRESA)")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por tipo")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ClienteDTO>> listarPorTipo(
            @Parameter(description = "Tipo de cliente: PARTICULAR o EMPRESA")
            @PathVariable TipoCliente tipo) {

        log.info("GET /api/clientes/tipo/{}", tipo);
        return ResponseEntity.ok(clienteService.listarPorTipo(tipo));
    }

    @Operation(summary = "Crear nuevo cliente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"id\":5,\"nombre\":\"Juan\",\"apellido\":\"Perez\",\"email\":\"juan.perez@mail.com\",\"telefono\":\"+56912345678\",\"tipo\":\"PARTICULAR\",\"rut\":\"12345678-9\",\"direccion\":\"Av. Siempre Viva 123\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email o RUT ya registrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\":\"Ya existe un cliente con ese email\"}"
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteDTO dto) {
        log.info("POST /api/clientes");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.crear(dto));
    }

    @Operation(summary = "Actualizar cliente existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "400", description = "Email o RUT ya en uso")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(
            @Parameter(description = "ID del cliente")
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO dto) {

        log.info("PUT /api/clientes/{}", id);
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del cliente")
            @PathVariable Long id) {

        log.info("DELETE /api/clientes/{}", id);
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}