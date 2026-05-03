package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.RegistroManoObra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroManoObraRepository extends JpaRepository<RegistroManoObra, Integer> {

    List<RegistroManoObra> findByProyectoId(Integer proyectoId);

    List<RegistroManoObra> findByItemId(Integer itemId);

    List<RegistroManoObra> findByItemIdAndActivoTrue(Integer itemId);

    List<RegistroManoObra> findByItemIdAndTipoCostoAndActivoTrue(Integer itemId, String tipoCosto);
}