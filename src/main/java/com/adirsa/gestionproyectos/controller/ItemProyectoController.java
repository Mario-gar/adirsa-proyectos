package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.Proyecto;
import com.adirsa.gestionproyectos.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.adirsa.gestionproyectos.repository.RegistroManoObraRepository;
import com.adirsa.gestionproyectos.repository.RegistroTransporteEquipoRepository;
import java.math.BigDecimal;

import java.math.BigDecimal;

@Controller
public class ItemProyectoController {

    private final ItemProyectoRepository itemProyectoRepository;
    private final ProyectoRepository proyectoRepository;
    private final PresupuestoItemDetalleRepository presupuestoItemDetalleRepository;
    private final RegistroEppRepository registroEppRepository;
    private final RegistroMaterialRepository registroMaterialRepository;
    private final RegistroManoObraRepository registroManoObraRepository;
    private final RegistroTransporteEquipoRepository registroTransporteEquipoRepository;
    private final RegistroSubcontratoRepository registroSubcontratoRepository;

    public ItemProyectoController(ItemProyectoRepository itemProyectoRepository,
                                  ProyectoRepository proyectoRepository,
                                  PresupuestoItemDetalleRepository presupuestoItemDetalleRepository,
                                  RegistroEppRepository registroEppRepository,
                                  RegistroMaterialRepository registroMaterialRepository,
                                  RegistroManoObraRepository registroManoObraRepository,RegistroTransporteEquipoRepository registroTransporteEquipoRepository,RegistroSubcontratoRepository registroSubcontratoRepository) {
        this.itemProyectoRepository = itemProyectoRepository;
        this.proyectoRepository = proyectoRepository;
        this.presupuestoItemDetalleRepository = presupuestoItemDetalleRepository;
        this.registroEppRepository = registroEppRepository;
        this.registroMaterialRepository = registroMaterialRepository;
        this.registroManoObraRepository = registroManoObraRepository;
        this.registroTransporteEquipoRepository = registroTransporteEquipoRepository;
        this.registroSubcontratoRepository = registroSubcontratoRepository;
    }
    @GetMapping("/proyectos/{proyectoId}/items/nuevo")
    public String mostrarFormularioNuevoItem(@PathVariable Integer proyectoId, Model model) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        ItemProyecto item = new ItemProyecto();
        item.setProyecto(proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("item", item);

        return "items/formulario";
    }

    @PostMapping("/proyectos/{proyectoId}/items/guardar")
    public String guardarItem(@PathVariable Integer proyectoId,
                              @ModelAttribute ItemProyecto item) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        item.setProyecto(proyecto);

        if (item.getActivo() == null) {
            item.setActivo(true);
        }

        item.calcularTotales();
        itemProyectoRepository.save(item);

        return "redirect:/proyectos/" + proyectoId;
    }

    @GetMapping("/items/{itemId}")
    public String verItem(@PathVariable Integer itemId, Model model) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        model.addAttribute("item", item);
        model.addAttribute("proyecto", item.getProyecto());

        model.addAttribute("epps",
                registroEppRepository.findByItemIdAndActivoTrue(itemId));

        model.addAttribute("materiales",
                registroMaterialRepository.findByItemIdAndActivoTrue(itemId));

        model.addAttribute("manoObras",
                registroManoObraRepository.findByItemIdAndTipoCostoAndActivoTrue(itemId, "DIRECTO"));

        model.addAttribute("transportesEquipos",
                registroTransporteEquipoRepository.findByItemIdAndActivoTrue(itemId));

        model.addAttribute("subcontratos",
                registroSubcontratoRepository.findByItemIdAndActivoTrue(itemId));

        BigDecimal materialReal = registroMaterialRepository
                .findByItemIdAndActivoTrue(itemId)
                .stream()
                .map(m -> m.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal manoObraReal = registroManoObraRepository
                .findByItemIdAndTipoCostoAndActivoTrue(itemId, "DIRECTO")
                .stream()
                .map(mo -> mo.getSalarioNeto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal transporteReal = registroTransporteEquipoRepository
                .findByItemIdAndActivoTrue(itemId)
                .stream()
                .map(te -> te.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal eppReal = registroEppRepository
                .findByItemIdAndActivoTrue(itemId)
                .stream()
                .map(e -> e.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subcontratoReal = registroSubcontratoRepository
                .findByItemIdAndActivoTrue(itemId)
                .stream()
                .map(s -> s.getMonto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReal = materialReal
                .add(manoObraReal)
                .add(transporteReal)
                .add(eppReal)
                .add(subcontratoReal);

        model.addAttribute("materialReal", materialReal);
        model.addAttribute("manoObraReal", manoObraReal);
        model.addAttribute("transporteReal", transporteReal);
        model.addAttribute("eppReal", eppReal);
        model.addAttribute("subcontratoReal", subcontratoReal);
        model.addAttribute("totalReal", totalReal);

        return "items/detalle";
    }

    @GetMapping("/items/{itemId}/editar")
    public String mostrarFormularioEditarItem(@PathVariable Integer itemId, Model model) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        model.addAttribute("item", item);
        model.addAttribute("proyecto", item.getProyecto());

        return "items/formulario";
    }

    @PostMapping("/items/{itemId}/desactivar")
    public String desactivarItem(@PathVariable Integer itemId) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        item.setActivo(false);
        itemProyectoRepository.save(item);

        return "redirect:/proyectos/" + item.getProyecto().getId();
    }

}