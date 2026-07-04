package com.constructapp.ms_calculo_material.controller;

import com.constructapp.ms_calculo_material.dto.CalculoMaterialDTO;
import com.constructapp.ms_calculo_material.service.CalculoMaterialService;
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
@RequestMapping("/api/calculos-material")
@RequiredArgsConstructor
@Tag(name = "Cálculo de Materiales", description = "Cálculo de cantidades y costos estimados de materiales por proyecto")
public class CalculoMaterialController {

    private final CalculoMaterialService calculoService;

    @Operation(summary = "Listar todos los cálculos de material")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<CalculoMaterialDTO>> listarTodos() {
        log.info("GET /api/calculos-material");
        return ResponseEntity.ok(calculoService.listarTodos());
    }

    @Operation(summary = "Obtener cálculo de material por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cálculo encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":1,\"proyectoId\":1,\"materialId\":3,\"nombreMaterial\":\"Cemento Portland\",\"cantidadCalculada\":50.0,\"unidadMedida\":\"saco 25kg\",\"precioEstimado\":325000.0,\"observacion\":\"Incluye 10% de merma\"}"
            ))),
        @ApiResponse(responseCode = "404", description = "Cálculo no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"Calculo no encontrado con id: 99\"}"
            )))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CalculoMaterialDTO> obtenerPorId(
            @Parameter(description = "ID del cálculo") @PathVariable Long id) {
        log.info("GET /api/calculos-material/{}", id);
        return ResponseEntity.ok(calculoService.obtenerPorId(id));
    }

    @Operation(summary = "Listar cálculos de material por proyecto")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por proyecto")
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<CalculoMaterialDTO>> listarPorProyecto(
            @Parameter(description = "ID del proyecto") @PathVariable Long proyectoId) {
        log.info("GET /api/calculos-material/proyecto/{}", proyectoId);
        return ResponseEntity.ok(calculoService.listarPorProyecto(proyectoId));
    }

    @Operation(summary = "Crear nuevo cálculo de material")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cálculo creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":5,\"proyectoId\":1,\"materialId\":3,\"nombreMaterial\":\"Cemento Portland\",\"cantidadCalculada\":50.0,\"unidadMedida\":\"saco 25kg\",\"precioEstimado\":325000.0,\"observacion\":\"Incluye 10% de merma\"}"
            ))),
        @ApiResponse(responseCode = "404", description = "El material no existe en el catálogo",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"El material con id 99 no existe en el catálogo\"}"
            ))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<CalculoMaterialDTO> crear(@Valid @RequestBody CalculoMaterialDTO dto) {
        log.info("POST /api/calculos-material");
        return ResponseEntity.status(HttpStatus.CREATED).body(calculoService.crear(dto));
    }

    @Operation(summary = "Actualizar cálculo de material existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cálculo actualizado"),
        @ApiResponse(responseCode = "404", description = "Cálculo no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CalculoMaterialDTO> actualizar(
            @Parameter(description = "ID del cálculo") @PathVariable Long id,
            @Valid @RequestBody CalculoMaterialDTO dto) {
        log.info("PUT /api/calculos-material/{}", id);
        return ResponseEntity.ok(calculoService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar cálculo de material")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cálculo eliminado"),
        @ApiResponse(responseCode = "404", description = "Cálculo no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del cálculo") @PathVariable Long id) {
        log.info("DELETE /api/calculos-material/{}", id);
        calculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
