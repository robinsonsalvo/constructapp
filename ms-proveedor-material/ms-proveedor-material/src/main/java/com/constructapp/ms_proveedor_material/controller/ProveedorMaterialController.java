package com.constructapp.ms_proveedor_material.controller;

import com.constructapp.ms_proveedor_material.dto.ProveedorMaterialDTO;
import com.constructapp.ms_proveedor_material.service.ProveedorMaterialService;
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
public class ProveedorMaterialController {

    private final ProveedorMaterialService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorMaterialDTO>> listarTodos() {
        log.info("GET /api/proveedores-material");
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorMaterialDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/proveedores-material/{}", id);
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProveedorMaterialDTO> crear(@Valid @RequestBody ProveedorMaterialDTO dto) {
        log.info("POST /api/proveedores-material");
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorMaterialDTO> actualizar(@PathVariable Long id,
            @Valid @RequestBody ProveedorMaterialDTO dto) {
        log.info("PUT /api/proveedores-material/{}", id);
        return ResponseEntity.ok(proveedorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/proveedores-material/{}", id);
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}