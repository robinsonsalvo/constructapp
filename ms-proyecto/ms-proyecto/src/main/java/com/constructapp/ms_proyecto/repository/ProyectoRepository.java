package com.constructapp.ms_proyecto.repository;


import com.constructapp.ms_proyecto.model.EstadoProyecto;
import com.constructapp.ms_proyecto.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByClienteId(Long clienteId);
    List<Proyecto> findByEstado(EstadoProyecto estado);
}
