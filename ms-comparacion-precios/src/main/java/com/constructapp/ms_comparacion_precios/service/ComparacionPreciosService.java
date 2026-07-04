package com.constructapp.ms_comparacion_precios.service;

import com.constructapp.ms_comparacion_precios.exception.ResourceNotFoundException;

import com.constructapp.ms_comparacion_precios.dto.ComparacionPrecioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComparacionPreciosService {

    private final WebClient webClient;

    public List<ComparacionPrecioDTO> compararPorMaterial(Long materialId) {
        log.info("Comparando precios para material id: {}", materialId);
        try {
            List<ComparacionPrecioDTO> precios = webClient.get()
                    .uri("/api/precios-proveedor/material/{materialId}", materialId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ComparacionPrecioDTO>>() {})
                    .block();

            if (precios == null || precios.isEmpty()) {
                log.info("No hay proveedores para el material id: {}", materialId);
                return List.of();
            }

            return precios.stream()
                    .sorted(Comparator.comparingDouble(ComparacionPrecioDTO::getPrecio))
                    .toList();

        } catch (Exception e) {
            log.error("Error al comparar precios: {}", e.getMessage());
            throw new RuntimeException("Error al obtener precios del material id: " + materialId);
        }
    }

    public ComparacionPrecioDTO obtenerMasBarato(Long materialId) {
        log.info("Obteniendo precio mas barato para material id: {}", materialId);
        List<ComparacionPrecioDTO> precios = compararPorMaterial(materialId);
        if (precios.isEmpty()) {
            throw new ResourceNotFoundException("No hay proveedores para el material id: " + materialId);
        }
        return precios.get(0);
    }
}