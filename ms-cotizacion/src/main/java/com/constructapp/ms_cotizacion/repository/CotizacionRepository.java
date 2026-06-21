package com.constructapp.ms_cotizacion.repository;

import com.constructapp.ms_cotizacion.model.Cotizacion;
import com.constructapp.ms_cotizacion.model.EstadoCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    List<Cotizacion> findByClienteId(Long clienteId);
    List<Cotizacion> findByProyectoId(Long proyectoId);
    List<Cotizacion> findByEstado(EstadoCotizacion estado);
}