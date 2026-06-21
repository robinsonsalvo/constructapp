package com.constructapp.ms_calculo_material.service;

import com.constructapp.ms_calculo_material.dto.CalculoMaterialDTO;
import com.constructapp.ms_calculo_material.model.CalculoMaterial;
import com.constructapp.ms_calculo_material.repository.CalculoMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculoMaterialService {

    private final CalculoMaterialRepository calculoRepository;
    private final WebClient webClient;

    public List<CalculoMaterialDTO> listarTodos() {
        log.info("Listando todos los calculos");
        return calculoRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<CalculoMaterialDTO> listarPorProyecto(Long proyectoId) {
        log.info("Listando calculos del proyecto id: {}", proyectoId);
        return calculoRepository.findByProyectoId(proyectoId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public CalculoMaterialDTO obtenerPorId(Long id) {
        log.info("Buscando calculo con id: {}", id);
        CalculoMaterial calculo = calculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calculo no encontrado con id: " + id));
        return convertirADTO(calculo);
    }

    public CalculoMaterialDTO crear(CalculoMaterialDTO dto) {
        log.info("Creando calculo para proyecto id: {} material id: {}",
                dto.getProyectoId(), dto.getMaterialId());

        // Verificar que no exista ya un cálculo para el mismo proyecto y material
        List<CalculoMaterial> existentes = calculoRepository.findByProyectoId(dto.getProyectoId());
        boolean yaExiste = existentes.stream()
                .anyMatch(c -> c.getMaterialId().equals(dto.getMaterialId()));
        if (yaExiste) {
            throw new RuntimeException(
                "Ya existe un cálculo para el material id: " + dto.getMaterialId() +
                " en el proyecto id: " + dto.getProyectoId() +
                ". Use la operación de actualizar.");
        }

        Map<String, Object> material = obtenerMaterial(dto.getMaterialId());

        Double precioReferencial = ((Number) material.get("precioReferencial")).doubleValue();
        Double precioEstimado = dto.getCantidadCalculada() * precioReferencial;

        CalculoMaterial calculo = convertirAEntidad(dto);
        calculo.setPrecioEstimado(precioEstimado);

        CalculoMaterial guardado = calculoRepository.save(calculo);
        log.info("Calculo creado con id: {}", guardado.getId());

        CalculoMaterialDTO response = convertirADTO(guardado);
        response.setNombreMaterial((String) material.get("nombre"));
        return response;
    }

    public CalculoMaterialDTO actualizar(Long id, CalculoMaterialDTO dto) {
        log.info("Actualizando calculo con id: {}", id);
        CalculoMaterial calculo = calculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calculo no encontrado con id: " + id));

        // Si cambia el material, verificar que no genere duplicado en el mismo proyecto
        if (!calculo.getMaterialId().equals(dto.getMaterialId())) {
            List<CalculoMaterial> existentes = calculoRepository.findByProyectoId(dto.getProyectoId());
            boolean yaExiste = existentes.stream()
                    .filter(c -> !c.getId().equals(id))
                    .anyMatch(c -> c.getMaterialId().equals(dto.getMaterialId()));
            if (yaExiste) {
                throw new RuntimeException(
                    "Ya existe un cálculo para el material id: " + dto.getMaterialId() +
                    " en el proyecto id: " + dto.getProyectoId());
            }
        }

        Map<String, Object> material = obtenerMaterial(dto.getMaterialId());
        Double precioReferencial = ((Number) material.get("precioReferencial")).doubleValue();
        Double precioEstimado = dto.getCantidadCalculada() * precioReferencial;

        calculo.setProyectoId(dto.getProyectoId());
        calculo.setMaterialId(dto.getMaterialId());
        calculo.setCantidadCalculada(dto.getCantidadCalculada());
        calculo.setUnidadMedida(dto.getUnidadMedida());
        calculo.setPrecioEstimado(precioEstimado);
        calculo.setObservacion(dto.getObservacion());

        CalculoMaterial actualizado = calculoRepository.save(calculo);
        log.info("Calculo actualizado con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando calculo con id: {}", id);
        if (!calculoRepository.existsById(id)) {
            throw new RuntimeException("Calculo no encontrado con id: " + id);
        }
        calculoRepository.deleteById(id);
        log.info("Calculo eliminado con id: {}", id);
    }

    private Map<String, Object> obtenerMaterial(Long materialId) {
        try {
            log.info("Obteniendo material id: {} de ms-catalogo", materialId);
            return webClient.get()
                    .uri("/api/materiales/{id}", materialId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Material id: {} no encontrado en ms-catalogo", materialId);
            throw new RuntimeException("El material con id " + materialId + " no existe en el catalogo");
        }
    }

    private CalculoMaterialDTO convertirADTO(CalculoMaterial calculo) {
        CalculoMaterialDTO dto = new CalculoMaterialDTO();
        dto.setId(calculo.getId());
        dto.setProyectoId(calculo.getProyectoId());
        dto.setMaterialId(calculo.getMaterialId());
        dto.setCantidadCalculada(calculo.getCantidadCalculada());
        dto.setUnidadMedida(calculo.getUnidadMedida());
        dto.setPrecioEstimado(calculo.getPrecioEstimado());
        dto.setObservacion(calculo.getObservacion());
        return dto;
    }

    private CalculoMaterial convertirAEntidad(CalculoMaterialDTO dto) {
        CalculoMaterial calculo = new CalculoMaterial();
        calculo.setProyectoId(dto.getProyectoId());
        calculo.setMaterialId(dto.getMaterialId());
        calculo.setCantidadCalculada(dto.getCantidadCalculada());
        calculo.setUnidadMedida(dto.getUnidadMedida());
        calculo.setObservacion(dto.getObservacion());
        return calculo;
    }
}
