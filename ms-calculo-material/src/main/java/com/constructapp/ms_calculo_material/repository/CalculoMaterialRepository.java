package com.constructapp.ms_calculo_material.repository;

import com.constructapp.ms_calculo_material.model.CalculoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculoMaterialRepository extends JpaRepository<CalculoMaterial, Long> {
    List<CalculoMaterial> findByProyectoId(Long proyectoId);
    List<CalculoMaterial> findByMaterialId(Long materialId);
}