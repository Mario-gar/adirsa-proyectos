package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.RegistroTransporteEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroTransporteEquipoRepository extends JpaRepository<RegistroTransporteEquipo, Integer> {

    List<RegistroTransporteEquipo> findByItemIdAndActivoTrue(Integer itemId);
}