package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.PresupuestoItemDetalle;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.PresupuestoItemDetalleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class PresupuestoItemDetalleController {

    private final PresupuestoItemDetalleRepository presupuestoItemDetalleRepository;
    private final ItemProyectoRepository itemProyectoRepository;

    public PresupuestoItemDetalleController(PresupuestoItemDetalleRepository presupuestoItemDetalleRepository,
                                            ItemProyectoRepository itemProyectoRepository) {
        this.presupuestoItemDetalleRepository = presupuestoItemDetalleRepository;
        this.itemProyectoRepository = itemProyectoRepository;
    }

    @GetMapping("/items/{itemId}/presupuesto-detalle/nuevo")
    public String mostrarFormularioNuevoDetalle(@PathVariable Integer itemId, Model model) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        PresupuestoItemDetalle detalle = new PresupuestoItemDetalle();
        detalle.setItem(item);

        model.addAttribute("item", item);
        model.addAttribute("proyecto", item.getProyecto());
        model.addAttribute("detalle", detalle);

        return "presupuesto-detalle/formulario";
    }

    @PostMapping("/items/{itemId}/presupuesto-detalle/guardar")
    public String guardarDetalle(@PathVariable Integer itemId,
                                 @ModelAttribute PresupuestoItemDetalle detalle) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        detalle.setItem(item);

        if (detalle.getCantidad() == null) {
            detalle.setCantidad(BigDecimal.ZERO);
        }

        if (detalle.getCostoUnitario() == null) {
            detalle.setCostoUnitario(BigDecimal.ZERO);
        }

        BigDecimal totalDetalle = detalle.getCantidad().multiply(detalle.getCostoUnitario());
        detalle.setTotal(totalDetalle);

        presupuestoItemDetalleRepository.save(detalle);

        recalcularTotalesItem(item);

        return "redirect:/items/" + itemId;
    }

    private void recalcularTotalesItem(ItemProyecto item) {

        List<PresupuestoItemDetalle> detalles =
                presupuestoItemDetalleRepository.findByItemId(item.getId());

        BigDecimal precioUnitario = detalles.stream()
                .map(PresupuestoItemDetalle::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cantidadItem = item.getCantidadPresupuestada();

        if (cantidadItem == null) {
            cantidadItem = BigDecimal.ZERO;
        }

        BigDecimal totalPresupuestado = cantidadItem.multiply(precioUnitario);

        item.setPrecioUnitarioPresupuestado(precioUnitario);
        item.setTotalPresupuestado(totalPresupuestado);

        itemProyectoRepository.save(item);
    }
}