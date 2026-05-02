package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.RegistroTransporteEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroTransporteEquipoRepository extends JpaRepository<RegistroTransporteEquipo, Integer> {

    List<RegistroTransporteEquipo> findByProyectoId(Integer proyectoId);

    List<RegistroTransporteEquipo> findByItemId(Integer itemId);

    List<RegistroTransporteEquipo> findByProyectoIdAndTipo(Integer proyectoId, String tipo);
}