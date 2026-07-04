package com.constructapp.ms_resena.controller;

import com.constructapp.ms_resena.dto.ResenaDTO;
import com.constructapp.ms_resena.service.ResenaService;
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
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Gestión de reseñas de clientes sobre proveedores de servicio")
public class ResenaController {

    private final ResenaService resenaService;

    @Operation(summary = "Listar todas las reseñas")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<ResenaDTO>> listarTodas() {
        log.info("GET /api/resenas");
        return ResponseEntity.ok(resenaService.listarTodas());
    }

    @Operation(summary = "Obtener reseña por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseña encontrada",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":1,\"clienteId\":2,\"proveedorServicioId\":1,\"puntuacion\":5,\"comentario\":\"Excelente trabajo, muy puntual\",\"fechaResena\":\"2026-06-20\"}"
            ))),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"Resena no encontrada con id: 99\"}"
            )))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> obtenerPorId(
            @Parameter(description = "ID de la reseña") @PathVariable Long id) {
        log.info("GET /api/resenas/{}", id);
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @Operation(summary = "Listar reseñas por proveedor de servicio")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por proveedor")
    @GetMapping("/proveedor/{proveedorServicioId}")
    public ResponseEntity<List<ResenaDTO>> listarPorProveedor(
            @Parameter(description = "ID del proveedor de servicio") @PathVariable Long proveedorServicioId) {
        log.info("GET /api/resenas/proveedor/{}", proveedorServicioId);
        return ResponseEntity.ok(resenaService.listarPorProveedor(proveedorServicioId));
    }

    @Operation(summary = "Listar reseñas realizadas por un cliente")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ResenaDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable Long clienteId) {
        log.info("GET /api/resenas/cliente/{}", clienteId);
        return ResponseEntity.ok(resenaService.listarPorCliente(clienteId));
    }

    @Operation(summary = "Crear nueva reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":5,\"clienteId\":2,\"proveedorServicioId\":1,\"puntuacion\":5,\"comentario\":\"Excelente trabajo, muy puntual\",\"fechaResena\":\"2026-07-04\"}"
            ))),
        @ApiResponse(responseCode = "404", description = "Cliente o proveedor no existe",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"No existe un cliente con id: 99\"}"
            ))),
        @ApiResponse(responseCode = "400", description = "El cliente ya reseñó a este proveedor / datos inválidos",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"El cliente ya realizó una reseña para este proveedor\"}"
            )))
    })
    @PostMapping
    public ResponseEntity<ResenaDTO> crear(@Valid @RequestBody ResenaDTO dto) {
        log.info("POST /api/resenas");
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(dto));
    }

    @Operation(summary = "Eliminar reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reseña eliminada"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la reseña") @PathVariable Long id) {
        log.info("DELETE /api/resenas/{}", id);
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
