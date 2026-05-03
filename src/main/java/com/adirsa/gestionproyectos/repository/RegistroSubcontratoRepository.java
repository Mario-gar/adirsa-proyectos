package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.RegistroSubcontrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroSubcontratoRepository extends JpaRepository<RegistroSubcontrato, Integer> {

    List<RegistroSubcontrato> findByItemIdAndActivoTrue(Integer itemId);

    List<RegistroSubcontrato> findByProyectoIdAndActivoTrue(Integer proyectoId);
}