package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.Proyecto;
import com.adirsa.gestionproyectos.entity.RegistroCostoIndirecto;
import com.adirsa.gestionproyectos.repository.ProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroCostoIndirectoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroCostoIndirectoController {

    private final RegistroCostoIndirectoRepository repository;
    private final ProyectoRepository proyectoRepository;

    public RegistroCostoIndirectoController(RegistroCostoIndirectoRepository repository,
                                            ProyectoRepository proyectoRepository) {
        this.repository = repository;
        this.proyectoRepository = proyectoRepository;
    }

    @GetMapping("/proyectos/{id}/costos-indirectos/nuevo")
    public String nuevo(@PathVariable Integer id, Model model) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        RegistroCostoIndirecto r = new RegistroCostoIndirecto();
        r.setProyecto(proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("costo", r);

        return "costos-indirectos/formulario";
    }

    @PostMapping("/proyectos/{id}/costos-indirectos/guardar")
    public String guardar(@PathVariable Integer id,
                          @ModelAttribute RegistroCostoIndirecto costo) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        costo.setProyecto(proyecto);

        if (costo.getActivo() == null) {
            costo.setActivo(true);
        }

        costo.calcular();
        repository.save(costo);

        return "redirect:/proyectos/" + id;
    }

    @GetMapping("/costos-indirectos/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {

        RegistroCostoIndirecto r = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        model.addAttribute("costo", r);
        model.addAttribute("proyecto", r.getProyecto());

        return "costos-indirectos/formulario";
    }

    @PostMapping("/costos-indirectos/{id}/desactivar")
    public String desactivar(@PathVariable Integer id) {

        RegistroCostoIndirecto r = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        r.setActivo(false);
        repository.save(r);

        return "redirect:/proyectos/" + r.getProyecto().getId();
    }
}