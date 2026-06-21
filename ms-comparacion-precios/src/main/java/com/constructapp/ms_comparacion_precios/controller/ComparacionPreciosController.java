package com.constructapp.ms_comparacion_precios.controller;

import com.constructapp.ms_comparacion_precios.dto.ComparacionPrecioDTO;
import com.constructapp.ms_comparacion_precios.service.ComparacionPreciosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comparacion-precios")
@RequiredArgsConstructor
public class ComparacionPreciosController {

    private final ComparacionPreciosService comparacionService;

    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<ComparacionPrecioDTO>> compararPorMaterial(
            @PathVariable Long materialId) {
        log.info("GET /api/comparacion-precios/material/{}", materialId);
        return ResponseEntity.ok(comparacionService.compararPorMaterial(materialId));
    }

    @GetMapping("/material/{materialId}/mas-barato")
    public ResponseEntity<ComparacionPrecioDTO> obtenerMasBarato(
            @PathVariable Long materialId) {
        log.info("GET /api/comparacion-precios/material/{}/mas-barato", materialId);
        return ResponseEntity.ok(comparacionService.obtenerMasBarato(materialId));
    }
}