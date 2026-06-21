package com.constructapp.ms_proyecto.controller;

import com.constructapp.ms_proyecto.dto.ProyectoDTO;
import com.constructapp.ms_proyecto.model.EstadoProyecto;
import com.constructapp.ms_proyecto.service.ProyectoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
@Tag(name = "Proyectos", description = "Gestión de proyectos de construcción. Estados: COTIZANDO → EN_EJECUCION → TERMINADO")
public class ProyectoController {

    private final ProyectoService proyectoService;

    @Operation(summary = "Listar todos los proyectos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<ProyectoDTO>> listarTodos() {
        log.info("GET /api/proyectos");
        return ResponseEntity.ok(proyectoService.listarTodos());
    }

    @Operation(summary = "Obtener proyecto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proyecto encontrado"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoDTO> obtenerPorId(
            @Parameter(description = "ID del proyecto") @PathVariable Long id) {
        log.info("GET /api/proyectos/{}", id);
        return ResponseEntity.ok(proyectoService.obtenerPorId(id));
    }

    @Operation(summary = "Listar proyectos por cliente")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ProyectoDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable Long clienteId) {
        log.info("GET /api/proyectos/cliente/{}", clienteId);
        return ResponseEntity.ok(proyectoService.listarPorCliente(clienteId));
    }

    @Operation(summary = "Listar proyectos por estado")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProyectoDTO>> listarPorEstado(
            @Parameter(description = "Estado: COTIZANDO, EN_EJECUCION, TERMINADO") @PathVariable EstadoProyecto estado) {
        log.info("GET /api/proyectos/estado/{}", estado);
        return ResponseEntity.ok(proyectoService.listarPorEstado(estado));
    }

    @Operation(summary = "Crear nuevo proyecto", description = "El proyecto inicia automáticamente en estado COTIZANDO")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Proyecto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o cliente no existe")
    })
    @PostMapping
    public ResponseEntity<ProyectoDTO> crear(@Valid @RequestBody ProyectoDTO dto) {
        log.info("POST /api/proyectos");
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.crear(dto));
    }

    @Operation(summary = "Actualizar proyecto", description = "Transiciones válidas: COTIZANDO→EN_EJECUCION, EN_EJECUCION→TERMINADO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proyecto actualizado"),
        @ApiResponse(responseCode = "400", description = "Transición de estado inválida"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProyectoDTO> actualizar(
            @Parameter(description = "ID del proyecto") @PathVariable Long id,
            @Valid @RequestBody ProyectoDTO dto) {
        log.info("PUT /api/proyectos/{}", id);
        return ResponseEntity.ok(proyectoService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar proyecto", description = "Solo se pueden eliminar proyectos en estado COTIZANDO")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proyecto eliminado"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar en el estado actual"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del proyecto") @PathVariable Long id) {
        log.info("DELETE /api/proyectos/{}", id);
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
