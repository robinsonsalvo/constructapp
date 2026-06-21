package com.constructapp.ms_resena.controller;

import com.constructapp.ms_resena.dto.ResenaDTO;
import com.constructapp.ms_resena.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @GetMapping
    public ResponseEntity<List<ResenaDTO>> listarTodas() {
        log.info("GET /api/resenas");
        return ResponseEntity.ok(resenaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/resenas/{}", id);
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @GetMapping("/proveedor/{proveedorServicioId}")
    public ResponseEntity<List<ResenaDTO>> listarPorProveedor(@PathVariable Long proveedorServicioId) {
        log.info("GET /api/resenas/proveedor/{}", proveedorServicioId);
        return ResponseEntity.ok(resenaService.listarPorProveedor(proveedorServicioId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ResenaDTO>> listarPorCliente(@PathVariable Long clienteId) {
        log.info("GET /api/resenas/cliente/{}", clienteId);
        return ResponseEntity.ok(resenaService.listarPorCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<ResenaDTO> crear(@Valid @RequestBody ResenaDTO dto) {
        log.info("POST /api/resenas");
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/resenas/{}", id);
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}