package com.constructapp.ms_proveedor_servicio.controller;

import com.constructapp.ms_proveedor_servicio.dto.ProveedorServicioDTO;
import com.constructapp.ms_proveedor_servicio.model.TipoServicio;
import com.constructapp.ms_proveedor_servicio.service.ProveedorServicioService;
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
@RequestMapping("/api/proveedores-servicio")
@RequiredArgsConstructor
@Tag(name = "Proveedores de Servicio", description = "Gestión de proveedores de servicios de construcción (electricidad, gasfitería, etc.)")
public class ProveedorServicioController {

    private final ProveedorServicioService proveedorServicioService;

    @Operation(summary = "Listar todos los proveedores de servicio")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<ProveedorServicioDTO>> listarTodos() {
        log.info("GET /api/proveedores-servicio");
        return ResponseEntity.ok(proveedorServicioService.listarTodos());
    }

    @Operation(summary = "Obtener proveedor de servicio por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":1,\"nombre\":\"Pedro\",\"apellido\":\"Soto\",\"rut\":\"11222333-4\",\"tipoServicio\":\"ELECTRICIDAD\",\"descripcion\":\"Instalaciones eléctricas residenciales\",\"precio\":25000.0,\"modalidad\":\"POR_HORA\",\"region\":\"METROPOLITANA\",\"disponible\":true}"
            ))),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"Proveedor de servicio no encontrado con id: 99\"}"
            )))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorServicioDTO> obtenerPorId(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        log.info("GET /api/proveedores-servicio/{}", id);
        return ResponseEntity.ok(proveedorServicioService.obtenerPorId(id));
    }

    @Operation(summary = "Listar proveedores por tipo de servicio")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por tipo de servicio")
    @GetMapping("/tipo/{tipoServicio}")
    public ResponseEntity<List<ProveedorServicioDTO>> listarPorTipo(
            @Parameter(description = "Tipo de servicio (ej. ELECTRICIDAD, GASFITERIA)") @PathVariable TipoServicio tipoServicio) {
        log.info("GET /api/proveedores-servicio/tipo/{}", tipoServicio);
        return ResponseEntity.ok(proveedorServicioService.listarPorTipo(tipoServicio));
    }

    @Operation(summary = "Listar proveedores actualmente disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<List<ProveedorServicioDTO>> listarDisponibles() {
        log.info("GET /api/proveedores-servicio/disponibles");
        return ResponseEntity.ok(proveedorServicioService.listarDisponibles());
    }

    @Operation(summary = "Listar proveedores por región")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por región")
    @GetMapping("/region/{region}")
    public ResponseEntity<List<ProveedorServicioDTO>> listarPorRegion(
            @Parameter(description = "Región (ej. METROPOLITANA)") @PathVariable String region) {
        log.info("GET /api/proveedores-servicio/region/{}", region);
        return ResponseEntity.ok(proveedorServicioService.listarPorRegion(region));
    }

    @Operation(summary = "Crear nuevo proveedor de servicio")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"id\":5,\"nombre\":\"Pedro\",\"apellido\":\"Soto\",\"rut\":\"11222333-4\",\"tipoServicio\":\"ELECTRICIDAD\",\"descripcion\":\"Instalaciones eléctricas residenciales\",\"precio\":25000.0,\"modalidad\":\"POR_HORA\",\"region\":\"METROPOLITANA\",\"disponible\":true}"
            ))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o RUT duplicado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                value = "{\"error\":\"Ya existe un proveedor con ese RUT\"}"
            )))
    })
    @PostMapping
    public ResponseEntity<ProveedorServicioDTO> crear(@Valid @RequestBody ProveedorServicioDTO dto) {
        log.info("POST /api/proveedores-servicio");
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorServicioService.crear(dto));
    }

    @Operation(summary = "Actualizar proveedor de servicio existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor actualizado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o RUT duplicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorServicioDTO> actualizar(
            @Parameter(description = "ID del proveedor") @PathVariable Long id,
            @Valid @RequestBody ProveedorServicioDTO dto) {
        log.info("PUT /api/proveedores-servicio/{}", id);
        return ResponseEntity.ok(proveedorServicioService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar proveedor de servicio")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proveedor eliminado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        log.info("DELETE /api/proveedores-servicio/{}", id);
        proveedorServicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
