package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.Proyecto;
import com.adirsa.gestionproyectos.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.adirsa.gestionproyectos.entity.ItemProyecto;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;

@Controller
public class ProyectoController {

    private final ProyectoRepository proyectoRepository;
    private final ItemProyectoRepository itemProyectoRepository;
    private final RegistroManoObraRepository registroManoObraRepository;
    private final RegistroEppRepository registroEppRepository;
    private final RegistroMaterialRepository registroMaterialRepository;
    private final RegistroTransporteEquipoRepository registroTransporteEquipoRepository;
    private final RegistroSubcontratoRepository registroSubcontratoRepository;

    public ProyectoController(ProyectoRepository proyectoRepository,
                              ItemProyectoRepository itemProyectoRepository, RegistroManoObraRepository registroManoObraRepository, RegistroEppRepository registroEppRepository, RegistroMaterialRepository registroMaterialRepository,RegistroTransporteEquipoRepository registroTransporteEquipoRepository,RegistroSubcontratoRepository registroSubcontratoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.itemProyectoRepository = itemProyectoRepository;
        this.registroManoObraRepository = registroManoObraRepository;
        this.registroEppRepository = registroEppRepository;
        this.registroMaterialRepository = registroMaterialRepository;
        this.registroTransporteEquipoRepository = registroTransporteEquipoRepository;
        this.registroSubcontratoRepository = registroSubcontratoRepository;
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

        Page<ItemProyecto> itemsPage = itemProyectoRepository.findByProyectoIdAndActivoTrue(
                id,
                PageRequest.of(page, 10)
        );

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("items", itemsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemsPage.getTotalPages());


        // 🔥 MANO DE OBRA INDIRECTA
        model.addAttribute(
                "manoObraIndirecta",
                registroManoObraRepository.findByProyectoIdAndTipoCostoAndActivoTrue(id, "INDIRECTO")
        );

        // 🔥 EPP GLOBAL
        model.addAttribute(
                "eppGlobal",
                registroEppRepository.findByProyectoIdAndItemIsNullAndActivoTrue(id)
        );

        // =========================
        // 🔥 RESUMEN GLOBAL
        // =========================

        BigDecimal presupuestoDirecto = itemsPage.getContent()
                .stream()
                .map(i -> i.getTotalPresupuestado() == null ? BigDecimal.ZERO : i.getTotalPresupuestado())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal costosIndirectosPresupuesto = proyecto.getPresupuestoCostosIndirectos() == null
                ? BigDecimal.ZERO
                : proyecto.getPresupuestoCostosIndirectos();

        BigDecimal administracionPresupuesto = proyecto.getPresupuestoAdministracion() == null
                ? BigDecimal.ZERO
                : proyecto.getPresupuestoAdministracion();

        BigDecimal utilidadPresupuesto = proyecto.getPresupuestoUtilidad() == null
                ? BigDecimal.ZERO
                : proyecto.getPresupuestoUtilidad();

        BigDecimal presupuestoTotal = presupuestoDirecto
                .add(costosIndirectosPresupuesto)
                .add(administracionPresupuesto)
                .add(utilidadPresupuesto);

        BigDecimal materialReal = registroMaterialRepository
                .findByProyectoId(id)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getActivo()))
                .map(m -> m.getTotal() == null ? BigDecimal.ZERO : m.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal manoObraDirecta = registroManoObraRepository
                .findByProyectoIdAndTipoCostoAndActivoTrue(id, "DIRECTO")
                .stream()
                .map(m -> m.getSalarioNeto() == null ? BigDecimal.ZERO : m.getSalarioNeto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal manoObraIndirectaTotal = registroManoObraRepository
                .findByProyectoIdAndTipoCostoAndActivoTrue(id, "INDIRECTO")
                .stream()
                .map(m -> m.getSalarioNeto() == null ? BigDecimal.ZERO : m.getSalarioNeto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal transporteReal = registroTransporteEquipoRepository
                .findByProyectoIdAndActivoTrue(id)
                .stream()
                .map(t -> t.getTotal() == null ? BigDecimal.ZERO : t.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal eppReal = registroEppRepository
                .findByProyectoId(id)
                .stream()
                .filter(e -> Boolean.TRUE.equals(e.getActivo()))
                .map(e -> e.getTotal() == null ? BigDecimal.ZERO : e.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subcontratosReal = registroSubcontratoRepository
                .findByProyectoIdAndActivoTrue(id)
                .stream()
                .map(s -> s.getMonto() == null ? BigDecimal.ZERO : s.getMonto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReal = materialReal
                .add(manoObraDirecta)
                .add(manoObraIndirectaTotal)
                .add(transporteReal)
                .add(eppReal)
                .add(subcontratosReal);

        BigDecimal diferencia = presupuestoTotal.subtract(totalReal);

        // 🔥 ENVIAR A VISTA
        model.addAttribute("presupuestoTotal", presupuestoTotal);
        model.addAttribute("materialRealProyecto", materialReal);
        model.addAttribute("manoObraDirectaProyecto", manoObraDirecta);
        model.addAttribute("manoObraIndirectaProyecto", manoObraIndirectaTotal);
        model.addAttribute("transporteProyecto", transporteReal);
        model.addAttribute("eppProyecto", eppReal);
        model.addAttribute("subcontratosProyecto", subcontratosReal);
        model.addAttribute("totalRealProyecto", totalReal);
        model.addAttribute("diferenciaProyecto", diferencia);
        model.addAttribute("presupuestoDirecto", presupuestoDirecto);
        model.addAttribute("presupuestoTotal", presupuestoTotal);

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