package com.constructapp.ms_cotizacion.controller;

import com.constructapp.ms_cotizacion.dto.CotizacionDTO;
import com.constructapp.ms_cotizacion.model.EstadoCotizacion;
import com.constructapp.ms_cotizacion.service.CotizacionService;
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
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
@Tag(name = "Cotizaciones", description = "Gestión de cotizaciones. Estados: BORRADOR → ENVIADA → APROBADA | RECHAZADA")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    @Operation(summary = "Listar todas las cotizaciones")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<CotizacionDTO>> listarTodas() {
        log.info("GET /api/cotizaciones");
        return ResponseEntity.ok(cotizacionService.listarTodas());
    }

    @Operation(summary = "Obtener cotización por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cotización encontrada"),
        @ApiResponse(responseCode = "404", description = "Cotización no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CotizacionDTO> obtenerPorId(
            @Parameter(description = "ID de la cotización") @PathVariable Long id) {
        log.info("GET /api/cotizaciones/{}", id);
        return ResponseEntity.ok(cotizacionService.obtenerPorId(id));
    }

    @Operation(summary = "Listar cotizaciones por cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CotizacionDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable Long clienteId) {
        log.info("GET /api/cotizaciones/cliente/{}", clienteId);
        return ResponseEntity.ok(cotizacionService.listarPorCliente(clienteId));
    }

    @Operation(summary = "Listar cotizaciones por proyecto")
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<CotizacionDTO>> listarPorProyecto(
            @Parameter(description = "ID del proyecto") @PathVariable Long proyectoId) {
        log.info("GET /api/cotizaciones/proyecto/{}", proyectoId);
        return ResponseEntity.ok(cotizacionService.listarPorProyecto(proyectoId));
    }

    @Operation(summary = "Crear nueva cotización", description = "Se crea automáticamente en estado BORRADOR")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cotización creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Cliente no existe o datos inválidos")
    })
    @PostMapping
    public ResponseEntity<CotizacionDTO> crear(@Valid @RequestBody CotizacionDTO dto) {
        log.info("POST /api/cotizaciones");
        return ResponseEntity.status(HttpStatus.CREATED).body(cotizacionService.crear(dto));
    }

    @Operation(summary = "Cambiar estado de cotización",
               description = "Transiciones válidas: BORRADOR→ENVIADA, ENVIADA→APROBADA, ENVIADA→RECHAZADA")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado cambiado correctamente"),
        @ApiResponse(responseCode = "400", description = "Transición de estado no permitida"),
        @ApiResponse(responseCode = "404", description = "Cotización no encontrada")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CotizacionDTO> cambiarEstado(
            @Parameter(description = "ID de la cotización") @PathVariable Long id,
            @Parameter(description = "Nuevo estado: ENVIADA, APROBADA, RECHAZADA") @RequestParam EstadoCotizacion estado) {
        log.info("PATCH /api/cotizaciones/{}/estado", id);
        return ResponseEntity.ok(cotizacionService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar cotización", description = "No se pueden eliminar cotizaciones APROBADAS")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cotización eliminada"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar en estado APROBADA"),
        @ApiResponse(responseCode = "404", description = "Cotización no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la cotización") @PathVariable Long id) {
        log.info("DELETE /api/cotizaciones/{}", id);
        cotizacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
