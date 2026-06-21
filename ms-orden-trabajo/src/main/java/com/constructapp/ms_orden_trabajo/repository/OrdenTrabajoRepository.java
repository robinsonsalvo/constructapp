package com.constructapp.ms_orden_trabajo.repository;

import com.constructapp.ms_orden_trabajo.model.OrdenTrabajo;
import com.constructapp.ms_orden_trabajo.model.EstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {
    List<OrdenTrabajo> findByProyectoId(Long proyectoId);
    List<OrdenTrabajo> findByEstado(EstadoOrden estado);
    boolean existsByCotizacionId(Long cotizacionId);
    Optional<OrdenTrabajo> findByCotizacionId(Long cotizacionId);
}