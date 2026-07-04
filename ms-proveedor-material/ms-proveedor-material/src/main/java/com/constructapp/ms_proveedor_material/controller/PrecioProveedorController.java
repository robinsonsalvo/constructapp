package com.constructapp.ms_proveedor_material.controller;

import com.constructapp.ms_proveedor_material.dto.PrecioProveedorDTO;
import com.constructapp.ms_proveedor_material.service.PrecioProveedorService;
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
@RequestMapping("/api/precios-proveedor")
@RequiredArgsConstructor
@Tag(name = "Precios de Proveedor", description = "Gestión de precios y stock ofrecidos por cada proveedor de material")
public class PrecioProveedorController {

    private final PrecioProveedorService precioService;

    @Operation(summary = "Listar precios por proveedor")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<PrecioProveedorDTO>> listarPorProveedor(
            @Parameter(description = "ID del proveedor") @PathVariable Long proveedorId) {
        log.info("GET /api/precios-proveedor/proveedor/{}", proveedorId);
        return ResponseEntity.ok(precioService.listarPorProveedor(proveedorId));
    }

    @Operation(summary = "Listar precios por material (comparación entre proveedores)")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(
            value = "[{\"id\":1,\"materialId\":3,\"nombreMaterial\":\"Cemento Portland\",\"precio\":6500.0,\"stockDisponible\":120,\"proveedorId\":1,\"nombreProveedor\":\"Ferretería El Constructor\"}]"
        )))
    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<PrecioProveedorDTO>> listarPorMaterial(
            @Parameter(description = "ID del material") @PathVariable Long materialId) {
        log.info("GET /api/precios-proveedor/material/{}", materialId);
        return ResponseEntity.ok(precioService.listarPorMaterial(materialId));
    }

    @Operation(summary = "Registrar precio de un material para un proveedor")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Precio registrado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":10,\"materialId\":3,\"nombreMaterial\":\"Cemento Portland\",\"precio\":6500.0,\"stockDisponible\":120,\"proveedorId\":1,\"nombreProveedor\":\"Ferretería El Constructor\"}"
            ))),
        @ApiResponse(responseCode = "404", description = "Proveedor o material no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"El material con id 99 no existe en el catálogo\"}"
            ))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<PrecioProveedorDTO> crear(@Valid @RequestBody PrecioProveedorDTO dto) {
        log.info("POST /api/precios-proveedor");
        return ResponseEntity.status(HttpStatus.CREATED).body(precioService.crear(dto));
    }

    @Operation(summary = "Actualizar precio o stock existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Precio actualizado"),
        @ApiResponse(responseCode = "404", description = "Precio, proveedor o material no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PrecioProveedorDTO> actualizar(
            @Parameter(description = "ID del registro de precio") @PathVariable Long id,
            @Valid @RequestBody PrecioProveedorDTO dto) {
        log.info("PUT /api/precios-proveedor/{}", id);
        return ResponseEntity.ok(precioService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar registro de precio")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Precio eliminado"),
        @ApiResponse(responseCode = "404", description = "Precio no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del registro de precio") @PathVariable Long id) {
        log.info("DELETE /api/precios-proveedor/{}", id);
        precioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
