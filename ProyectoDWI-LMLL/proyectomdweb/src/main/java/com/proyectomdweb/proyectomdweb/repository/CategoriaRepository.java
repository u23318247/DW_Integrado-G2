package com.proyectomdweb.proyectomdweb.repository;

import com.proyectomdweb.proyectomdweb.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {}