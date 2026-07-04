package com.constructapp.ms_proveedor_material.controller;

import com.constructapp.ms_proveedor_material.dto.ProveedorMaterialDTO;
import com.constructapp.ms_proveedor_material.service.ProveedorMaterialService;
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
@RequestMapping("/api/proveedores-material")
@RequiredArgsConstructor
@Tag(name = "Proveedores de Material", description = "Gestión de proveedores de materiales de construcción")
public class ProveedorMaterialController {

    private final ProveedorMaterialService proveedorService;

    @Operation(summary = "Listar todos los proveedores de material")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<ProveedorMaterialDTO>> listarTodos() {
        log.info("GET /api/proveedores-material");
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    @Operation(summary = "Obtener proveedor de material por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":1,\"nombre\":\"Ferretería El Constructor\",\"rut\":\"76123456-7\",\"email\":\"contacto@elconstructor.cl\",\"telefono\":\"+56221234567\",\"direccion\":\"Av. Industrial 500\",\"region\":\"METROPOLITANA\"}"
            ))),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"Proveedor no encontrado con id: 99\"}"
            )))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorMaterialDTO> obtenerPorId(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        log.info("GET /api/proveedores-material/{}", id);
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @Operation(summary = "Crear nuevo proveedor de material")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":5,\"nombre\":\"Ferretería El Constructor\",\"rut\":\"76123456-7\",\"email\":\"contacto@elconstructor.cl\",\"telefono\":\"+56221234567\",\"direccion\":\"Av. Industrial 500\",\"region\":\"METROPOLITANA\"}"
            ))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos, nombre/RUT/email duplicado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"Ya existe un proveedor con ese RUT\"}"
            )))
    })
    @PostMapping
    public ResponseEntity<ProveedorMaterialDTO> crear(@Valid @RequestBody ProveedorMaterialDTO dto) {
        log.info("POST /api/proveedores-material");
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(dto));
    }

    @Operation(summary = "Actualizar proveedor de material existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor actualizado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o duplicados")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorMaterialDTO> actualizar(
            @Parameter(description = "ID del proveedor") @PathVariable Long id,
            @Valid @RequestBody ProveedorMaterialDTO dto) {
        log.info("PUT /api/proveedores-material/{}", id);
        return ResponseEntity.ok(proveedorService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar proveedor de material")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proveedor eliminado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        log.info("DELETE /api/proveedores-material/{}", id);
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
