package com.constructapp.ms_resena.repository;

import com.constructapp.ms_resena.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProveedorServicioId(Long proveedorServicioId);
    List<Resena> findByClienteId(Long clienteId);
}