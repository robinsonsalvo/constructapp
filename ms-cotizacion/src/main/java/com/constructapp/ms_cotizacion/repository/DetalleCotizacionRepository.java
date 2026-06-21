package com.constructapp.ms_cotizacion.repository;

import com.constructapp.ms_cotizacion.model.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {
    List<DetalleCotizacion> findByCotizacionId(Long cotizacionId);
}