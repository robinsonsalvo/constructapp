package com.constructapp.ms_proveedor_material.repository;

import com.constructapp.ms_proveedor_material.model.ProveedorMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorMaterialRepository extends JpaRepository<ProveedorMaterial, Long>{

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);

}
