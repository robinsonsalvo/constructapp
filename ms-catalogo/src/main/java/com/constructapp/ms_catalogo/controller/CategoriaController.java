package com.constructapp.ms_catalogo.controller;

import com.constructapp.ms_catalogo.dto.CategoriaDTO;
import com.constructapp.ms_catalogo.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de categorías de materiales")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Operation(summary = "Listar todas las categorías")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        log.info("GET /api/categorias");
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @Operation(summary = "Obtener categoría por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerPorId(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        log.info("GET /api/categorias/{}", id);
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @Operation(summary = "Crear nueva categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado")
    })
    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaDTO dto) {
        log.info("POST /api/categorias");
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
    }

    @Operation(summary = "Actualizar categoría existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(
            @Parameter(description = "ID de la categoría") @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO dto) {
        log.info("PUT /api/categorias/{}", id);
        return ResponseEntity.ok(categoriaService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        log.info("DELETE /api/categorias/{}", id);
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
