package com.constructapp.ms_catalogo.repository;

import com.constructapp.ms_catalogo.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository  extends JpaRepository<Material, Long> {
    List<Material> findByCategoriaId(Long categoriaId);
    boolean existsByNombre(String nombre);

}
