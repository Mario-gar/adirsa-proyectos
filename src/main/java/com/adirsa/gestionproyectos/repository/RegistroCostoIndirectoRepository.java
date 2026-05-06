package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.RegistroCostoIndirecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroCostoIndirectoRepository extends JpaRepository<RegistroCostoIndirecto, Integer> {

    List<RegistroCostoIndirecto> findByProyectoIdAndActivoTrue(Integer proyectoId);
}