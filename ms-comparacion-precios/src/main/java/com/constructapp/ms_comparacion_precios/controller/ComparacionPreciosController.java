package com.constructapp.ms_comparacion_precios.controller;

import com.constructapp.ms_comparacion_precios.dto.ComparacionPrecioDTO;
import com.constructapp.ms_comparacion_precios.service.ComparacionPreciosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comparacion-precios")
@RequiredArgsConstructor
@Tag(name = "Comparación de Precios", description = "Compara precios de un material entre distintos proveedores (consume ms-proveedor-material)")
public class ComparacionPreciosController {

    private final ComparacionPreciosService comparacionService;

    @Operation(summary = "Comparar precios de un material entre todos los proveedores")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comparación obtenida correctamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "[{\"precioProveedorId\":10,\"materialId\":3,\"nombreProveedor\":\"Ferretería El Constructor\",\"precio\":6500.0,\"stockDisponible\":120,\"proveedorId\":1},{\"precioProveedorId\":11,\"materialId\":3,\"nombreProveedor\":\"MaterialesPro\",\"precio\":6200.0,\"stockDisponible\":80,\"proveedorId\":2}]"
            ))),
        @ApiResponse(responseCode = "404", description = "No hay proveedores para el material",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"No hay proveedores para el material id: 99\"}"
            )))
    })
    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<ComparacionPrecioDTO>> compararPorMaterial(
            @Parameter(description = "ID del material") @PathVariable Long materialId) {
        log.info("GET /api/comparacion-precios/material/{}", materialId);
        return ResponseEntity.ok(comparacionService.compararPorMaterial(materialId));
    }

    @Operation(summary = "Obtener el proveedor con el precio más bajo para un material")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor más barato encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"precioProveedorId\":11,\"materialId\":3,\"nombreProveedor\":\"MaterialesPro\",\"precio\":6200.0,\"stockDisponible\":80,\"proveedorId\":2}"
            ))),
        @ApiResponse(responseCode = "404", description = "No hay proveedores para el material")
    })
    @GetMapping("/material/{materialId}/mas-barato")
    public ResponseEntity<ComparacionPrecioDTO> obtenerMasBarato(
            @Parameter(description = "ID del material") @PathVariable Long materialId) {
        log.info("GET /api/comparacion-precios/material/{}/mas-barato", materialId);
        return ResponseEntity.ok(comparacionService.obtenerMasBarato(materialId));
    }
}
