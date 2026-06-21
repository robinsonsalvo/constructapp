package com.constructapp.ms_cliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.constructapp.ms_cliente.model.Cliente;
import com.constructapp.ms_cliente.model.TipoCliente;

import java.util.List;


public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmail(String email);
    boolean existsByRut(String rut);
    List<Cliente> findByTipo(TipoCliente tipo);
}
