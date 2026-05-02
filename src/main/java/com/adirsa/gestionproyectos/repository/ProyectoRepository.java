package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    Optional<Proyecto> findByCodigo(String codigo);

    List<Proyecto> findByEstado(String estado);
}