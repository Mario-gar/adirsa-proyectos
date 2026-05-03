package com.adirsa.gestionproyectos.repository;

import com.adirsa.gestionproyectos.entity.PresupuestoItemDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresupuestoItemDetalleRepository extends JpaRepository<PresupuestoItemDetalle, Integer> {

    List<PresupuestoItemDetalle> findByItemId(Integer itemId);

    List<PresupuestoItemDetalle> findByItemIdAndCategoria(Integer itemId, String categoria);
}