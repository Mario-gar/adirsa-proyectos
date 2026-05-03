package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.RegistroEpp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroEppRepository extends JpaRepository<RegistroEpp, Integer> {

    List<RegistroEpp> findByItemId(Integer itemId);

    List<RegistroEpp> findByProyectoId(Integer proyectoId);

    List<RegistroEpp> findByItemIdAndActivoTrue(Integer itemId);

    List<RegistroEpp> findByProyectoIdAndItemIsNullAndActivoTrue(Integer proyectoId);
}