package com.constructapp.ms_catalogo.controller;

import com.constructapp.ms_catalogo.dto.MaterialDTO;
import com.constructapp.ms_catalogo.service.MaterialService;
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
@RequestMapping("/api/materiales")
@RequiredArgsConstructor
@Tag(name = "Materiales", description = "Gestión de materiales de construcción")
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "Listar todos los materiales")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<MaterialDTO>> listarTodos() {
        log.info("GET /api/materiales");
        return ResponseEntity.ok(materialService.listarTodos());
    }

    @Operation(summary = "Obtener material por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Material encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                name = "Ejemplo de material",
                value = "{\"id\":1,\"nombre\":\"Cemento Portland\",\"unidadMedida\":\"saco 25kg\",\"precioReferencial\":6500.0,\"descripcion\":\"Cemento de uso general\",\"categoriaId\":1}"
            ))),
        @ApiResponse(responseCode = "404", description = "Material no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"Material no encontrado con id: 99\"}"
            )))
    })
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> obtenerPorId(
            @Parameter(description = "ID del material") @PathVariable Long id) {
        log.info("GET /api/materiales/{}", id);
        return ResponseEntity.ok(materialService.obtenerPorId(id));
    }

    @Operation(summary = "Listar materiales por categoría")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por categoría")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<MaterialDTO>> listarPorCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Long categoriaId) {
        log.info("GET /api/materiales/categoria/{}", categoriaId);
        return ResponseEntity.ok(materialService.listarPorCategoria(categoriaId));
    }

    @Operation(summary = "Crear nuevo material")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Material creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                name = "Material creado",
                value = "{\"id\":5,\"nombre\":\"Cemento Portland\",\"unidadMedida\":\"saco 25kg\",\"precioReferencial\":6500.0,\"descripcion\":\"Cemento de uso general\",\"categoriaId\":1}"
            ))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"Ya existe un material con ese nombre\"}"
            ))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PostMapping
    public ResponseEntity<MaterialDTO> crear(@Valid @RequestBody MaterialDTO dto) {
        log.info("POST /api/materiales");
        return ResponseEntity.status(HttpStatus.CREATED).body(materialService.crear(dto));
    }

    @Operation(summary = "Actualizar material existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Material actualizado"),
        @ApiResponse(responseCode = "404", description = "Material no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MaterialDTO> actualizar(
            @Parameter(description = "ID del material") @PathVariable Long id,
            @Valid @RequestBody MaterialDTO dto) {
        log.info("PUT /api/materiales/{}", id);
        return ResponseEntity.ok(materialService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar material")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Material eliminado"),
        @ApiResponse(responseCode = "404", description = "Material no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del material") @PathVariable Long id) {
        log.info("DELETE /api/materiales/{}", id);
        materialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
