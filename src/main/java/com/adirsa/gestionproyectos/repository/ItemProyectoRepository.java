package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemProyectoRepository extends JpaRepository<ItemProyecto, Integer> {

    List<ItemProyecto> findByProyecto(Proyecto proyecto);

    List<ItemProyecto> findByProyectoId(Integer proyectoId);

    List<ItemProyecto> findByProyectoIdAndActivoTrue(Integer proyectoId);

    Page<ItemProyecto> findByProyectoId(Integer proyectoId, Pageable pageable);

    Page<ItemProyecto> findByProyectoIdAndActivoTrue(Integer proyectoId, Pageable pageable);
}