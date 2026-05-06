package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.MovimientoPendiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoPendienteRepository extends JpaRepository<MovimientoPendiente, Integer> {

    List<MovimientoPendiente> findByProyectoIdAndActivoTrue(Integer proyectoId);

    List<MovimientoPendiente> findByProyectoIdAndEstadoAndActivoTrue(Integer proyectoId, String estado);
}