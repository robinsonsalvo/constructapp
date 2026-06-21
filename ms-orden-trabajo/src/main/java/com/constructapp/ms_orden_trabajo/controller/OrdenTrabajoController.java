package com.constructapp.ms_orden_trabajo.controller;

import com.constructapp.ms_orden_trabajo.dto.OrdenTrabajoDTO;
import com.constructapp.ms_orden_trabajo.model.EstadoOrden;
import com.constructapp.ms_orden_trabajo.service.OrdenTrabajoService;
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
@RequestMapping("/api/ordenes-trabajo")
@RequiredArgsConstructor
@Tag(name = "Órdenes de Trabajo", description = "Gestión de órdenes de trabajo. Estados: PENDIENTE → EN_CURSO → COMPLETADA | CANCELADA")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenService;

    @Operation(summary = "Listar todas las órdenes de trabajo")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<OrdenTrabajoDTO>> listarTodas() {
        log.info("GET /api/ordenes-trabajo");
        return ResponseEntity.ok(ordenService.listarTodas());
    }

    @Operation(summary = "Obtener orden por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> obtenerPorId(
            @Parameter(description = "ID de la orden") @PathVariable Long id) {
        log.info("GET /api/ordenes-trabajo/{}", id);
        return ResponseEntity.ok(ordenService.obtenerPorId(id));
    }

    @Operation(summary = "Listar órdenes por proyecto")
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<OrdenTrabajoDTO>> listarPorProyecto(
            @Parameter(description = "ID del proyecto") @PathVariable Long proyectoId) {
        log.info("GET /api/ordenes-trabajo/proyecto/{}", proyectoId);
        return ResponseEntity.ok(ordenService.listarPorProyecto(proyectoId));
    }

    @Operation(summary = "Listar órdenes por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrdenTrabajoDTO>> listarPorEstado(
            @Parameter(description = "Estado: PENDIENTE, EN_CURSO, COMPLETADA, CANCELADA") @PathVariable EstadoOrden estado) {
        log.info("GET /api/ordenes-trabajo/estado/{}", estado);
        return ResponseEntity.ok(ordenService.listarPorEstado(estado));
    }

    @Operation(summary = "Crear nueva orden de trabajo", description = "Requiere una cotización APROBADA. Se crea en estado PENDIENTE")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Ya existe una orden para esa cotización")
    })
    @PostMapping
    public ResponseEntity<OrdenTrabajoDTO> crear(@Valid @RequestBody OrdenTrabajoDTO dto) {
        log.info("POST /api/ordenes-trabajo");
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(dto));
    }

    @Operation(summary = "Cambiar estado de orden",
               description = "Transiciones válidas: PENDIENTE→EN_CURSO|CANCELADA, EN_CURSO→COMPLETADA|CANCELADA")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado cambiado correctamente"),
        @ApiResponse(responseCode = "400", description = "Transición de estado no permitida"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenTrabajoDTO> cambiarEstado(
            @Parameter(description = "ID de la orden") @PathVariable Long id,
            @Parameter(description = "Nuevo estado") @RequestParam EstadoOrden estado) {
        log.info("PATCH /api/ordenes-trabajo/{}/estado", id);
        return ResponseEntity.ok(ordenService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar orden", description = "Solo se pueden eliminar órdenes PENDIENTES o CANCELADAS")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Orden eliminada"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar en el estado actual"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la orden") @PathVariable Long id) {
        log.info("DELETE /api/ordenes-trabajo/{}", id);
        ordenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
