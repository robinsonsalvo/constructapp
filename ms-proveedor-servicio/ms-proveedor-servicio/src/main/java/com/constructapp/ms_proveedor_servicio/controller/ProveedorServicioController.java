package com.constructapp.ms_proveedor_servicio.controller;


import com.constructapp.ms_proveedor_servicio.dto.ProveedorServicioDTO;
import com.constructapp.ms_proveedor_servicio.model.TipoServicio;
import com.constructapp.ms_proveedor_servicio.service.ProveedorServicioService;
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
public class ProveedorServicioController {

    private final ProveedorServicioService proveedorServicioService;

    @GetMapping
    public ResponseEntity<List<ProveedorServicioDTO>> listarTodos() {
        log.info("GET /api/proveedores-servicio");
        return ResponseEntity.ok(proveedorServicioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorServicioDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/proveedores-servicio/{}", id);
        return ResponseEntity.ok(proveedorServicioService.obtenerPorId(id));
    }

    @GetMapping("/tipo/{tipoServicio}")
    public ResponseEntity<List<ProveedorServicioDTO>> listarPorTipo(@PathVariable TipoServicio tipoServicio) {
        log.info("GET /api/proveedores-servicio/tipo/{}", tipoServicio);
        return ResponseEntity.ok(proveedorServicioService.listarPorTipo(tipoServicio));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ProveedorServicioDTO>> listarDisponibles() {
        log.info("GET /api/proveedores-servicio/disponibles");
        return ResponseEntity.ok(proveedorServicioService.listarDisponibles());
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<ProveedorServicioDTO>> listarPorRegion(@PathVariable String region) {
        log.info("GET /api/proveedores-servicio/region/{}", region);
        return ResponseEntity.ok(proveedorServicioService.listarPorRegion(region));
    }

    @PostMapping
    public ResponseEntity<ProveedorServicioDTO> crear(@Valid @RequestBody ProveedorServicioDTO dto) {
        log.info("POST /api/proveedores-servicio");
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorServicioService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorServicioDTO> actualizar(@PathVariable Long id,
                                                            @Valid @RequestBody ProveedorServicioDTO dto) {
        log.info("PUT /api/proveedores-servicio/{}", id);
        return ResponseEntity.ok(proveedorServicioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/proveedores-servicio/{}", id);
        proveedorServicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
