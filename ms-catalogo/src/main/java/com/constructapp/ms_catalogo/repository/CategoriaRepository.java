package com.constructapp.ms_catalogo.repository;

import com.constructapp.ms_catalogo.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoriaRepository  extends JpaRepository<Categoria, Long> {
    boolean existsByNombre(String nombre);
}
