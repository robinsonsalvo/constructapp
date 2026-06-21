package com.constructapp.ms_proveedor_servicio.repository;


import com.constructapp.ms_proveedor_servicio.model.ProveedorServicio;
import com.constructapp.ms_proveedor_servicio.model.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorServicioRepository extends JpaRepository<ProveedorServicio, Long> {
    boolean existsByRut(String rut);
    List<ProveedorServicio> findByTipoServicio(TipoServicio tipoServicio);
    List<ProveedorServicio> findByDisponible(Boolean disponible);
    List<ProveedorServicio> findByRegion(String region);
}
