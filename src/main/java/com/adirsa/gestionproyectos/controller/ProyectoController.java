package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.Proyecto;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.ProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroEppRepository;
import com.adirsa.gestionproyectos.repository.RegistroManoObraRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class ProyectoController {

    private final ProyectoRepository proyectoRepository;
    private final ItemProyectoRepository itemProyectoRepository;
    private final RegistroManoObraRepository registroManoObraRepository;
    private final RegistroEppRepository registroEppRepository;

    public ProyectoController(ProyectoRepository proyectoRepository,
                              ItemProyectoRepository itemProyectoRepository, RegistroManoObraRepository registroManoObraRepository, RegistroEppRepository registroEppRepository) {
        this.proyectoRepository = proyectoRepository;
        this.itemProyectoRepository = itemProyectoRepository;
        this.registroManoObraRepository = registroManoObraRepository;
        this.registroEppRepository = registroEppRepository;
    }

    @GetMapping("/proyectos")
    public String listarProyectos(Model model) {
        model.addAttribute("proyectos", proyectoRepository.findAll());
        return "proyectos/lista";
    }

    @GetMapping("/proyectos/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("proyecto", new Proyecto());
        return "proyectos/formulario";
    }

    @PostMapping("/proyectos/guardar")
    public String guardarProyecto(@ModelAttribute Proyecto proyecto) {

        if (proyecto.getPresupuestoTotal() == null) {
            proyecto.setPresupuestoTotal(BigDecimal.ZERO);
        }

        proyectoRepository.save(proyecto);
        return "redirect:/proyectos";
    }

    @GetMapping("/proyectos/{id}")
    public String verProyecto(@PathVariable Integer id,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        var itemsPage = itemProyectoRepository.findByProyectoIdAndActivoTrue(
                id,
                PageRequest.of(page, 10)
        );

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("items", itemsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemsPage.getTotalPages());

        // 🔥 NUEVO
        model.addAttribute(
                "manoObraIndirecta",
                registroManoObraRepository.findByProyectoIdAndTipoCostoAndActivoTrue(id, "INDIRECTO")
        );

        model.addAttribute(
                "eppGlobal",
                registroEppRepository.findByProyectoIdAndItemIsNullAndActivoTrue(id)
        );

        return "proyectos/detalle";
    }

    @GetMapping("/proyectos/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        model.addAttribute("proyecto", proyecto);
        return "proyectos/formulario";
    }

    @PostMapping("/proyectos/{id}/cancelar")
    public String cancelarProyecto(@PathVariable Integer id) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setEstado("CANCELADO");
        proyectoRepository.save(proyecto);

        return "redirect:/proyectos/" + id;
    }
}