package com.constructapp.ms_proveedor_material.controller;

import com.constructapp.ms_proveedor_material.dto.PrecioProveedorDTO;
import com.constructapp.ms_proveedor_material.service.PrecioProveedorService;
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
public class PrecioProveedorController {

    private final PrecioProveedorService precioService;

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<PrecioProveedorDTO>> listarPorProveedor(@PathVariable Long proveedorId) {
        log.info("GET /api/precios-proveedor/proveedor/{}", proveedorId);
        return ResponseEntity.ok(precioService.listarPorProveedor(proveedorId));
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<PrecioProveedorDTO>> listarPorMaterial(@PathVariable Long materialId) {
        log.info("GET /api/precios-proveedor/material/{}", materialId);
        return ResponseEntity.ok(precioService.listarPorMaterial(materialId));
    }

    @PostMapping
    public ResponseEntity<PrecioProveedorDTO> crear(@Valid @RequestBody PrecioProveedorDTO dto) {
        log.info("POST /api/precios-proveedor");
        return ResponseEntity.status(HttpStatus.CREATED).body(precioService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrecioProveedorDTO> actualizar(@PathVariable Long id,
            @Valid @RequestBody PrecioProveedorDTO dto) {
        log.info("PUT /api/precios-proveedor/{}", id);
        return ResponseEntity.ok(precioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/precios-proveedor/{}", id);
        precioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}