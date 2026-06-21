package com.constructapp.ms_calculo_material.controller;

import com.constructapp.ms_calculo_material.dto.CalculoMaterialDTO;
import com.constructapp.ms_calculo_material.service.CalculoMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/calculos-material")
@RequiredArgsConstructor
public class CalculoMaterialController {

    private final CalculoMaterialService calculoService;

    @GetMapping
    public ResponseEntity<List<CalculoMaterialDTO>> listarTodos() {
        log.info("GET /api/calculos-material");
        return ResponseEntity.ok(calculoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalculoMaterialDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/calculos-material/{}", id);
        return ResponseEntity.ok(calculoService.obtenerPorId(id));
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<CalculoMaterialDTO>> listarPorProyecto(@PathVariable Long proyectoId) {
        log.info("GET /api/calculos-material/proyecto/{}", proyectoId);
        return ResponseEntity.ok(calculoService.listarPorProyecto(proyectoId));
    }

    @PostMapping
    public ResponseEntity<CalculoMaterialDTO> crear(@Valid @RequestBody CalculoMaterialDTO dto) {
        log.info("POST /api/calculos-material");
        return ResponseEntity.status(HttpStatus.CREATED).body(calculoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalculoMaterialDTO> actualizar(@PathVariable Long id,
            @Valid @RequestBody CalculoMaterialDTO dto) {
        log.info("PUT /api/calculos-material/{}", id);
        return ResponseEntity.ok(calculoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/calculos-material/{}", id);
        calculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}