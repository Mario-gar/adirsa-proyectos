package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.RegistroMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroMaterialRepository extends JpaRepository<RegistroMaterial, Integer> {

    List<RegistroMaterial> findByProyectoId(Integer proyectoId);

    List<RegistroMaterial> findByItemId(Integer itemId);
}