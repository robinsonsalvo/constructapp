package com.constructapp.ms_proveedor_material.repository;

import com.constructapp.ms_proveedor_material.model.PrecioProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface PrecioProveedorRepository extends JpaRepository<PrecioProveedor, Long> {

    List<PrecioProveedor> findByProveedorId(Long proveedorId);

    List<PrecioProveedor> findByMaterialId(Long materialId);

    boolean existsByProveedorIdAndMaterialId(Long proveedorId, Long materialId);
}
