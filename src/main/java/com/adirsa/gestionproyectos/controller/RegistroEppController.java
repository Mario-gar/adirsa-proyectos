package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.Proyecto;
import com.adirsa.gestionproyectos.entity.RegistroEpp;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.ProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroEppRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroEppController {

    private final RegistroEppRepository registroEppRepository;
    private final ItemProyectoRepository itemProyectoRepository;
    private final ProyectoRepository proyectoRepository;

    public RegistroEppController(RegistroEppRepository registroEppRepository,
                                 ItemProyectoRepository itemProyectoRepository,
                                 ProyectoRepository proyectoRepository) {
        this.registroEppRepository = registroEppRepository;
        this.itemProyectoRepository = itemProyectoRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @GetMapping("/items/{itemId}/epp/nuevo")
    public String nuevoEpp(@PathVariable Integer itemId, Model model) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        RegistroEpp epp = new RegistroEpp();
        epp.setItem(item);
        epp.setProyecto(item.getProyecto());

        model.addAttribute("item", item);
        model.addAttribute("proyecto", item.getProyecto());
        model.addAttribute("epp", epp);

        return "epp/formulario";
    }

    @PostMapping("/items/{itemId}/epp/guardar")
    public String guardarEpp(@PathVariable Integer itemId,
                             @ModelAttribute RegistroEpp epp) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        epp.setItem(item);
        epp.setProyecto(item.getProyecto());

        if (epp.getActivo() == null) {
            epp.setActivo(true);
        }

        epp.calcular();
        registroEppRepository.save(epp);

        return "redirect:/items/" + itemId;
    }

    @GetMapping("/epp/{id}/editar")
    public String editarEpp(@PathVariable Integer id, Model model) {

        RegistroEpp epp = registroEppRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EPP no encontrado"));

        model.addAttribute("epp", epp);
        model.addAttribute("proyecto", epp.getProyecto());

        if (epp.getItem() != null) {
            model.addAttribute("item", epp.getItem());
            return "epp/formulario";
        }

        return "epp/formulario-global";
    }

    @PostMapping("/epp/{id}/desactivar")
    public String desactivarEpp(@PathVariable Integer id) {

        RegistroEpp epp = registroEppRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EPP no encontrado"));

        epp.setActivo(false);
        registroEppRepository.save(epp);

        if (epp.getItem() != null) {
            return "redirect:/items/" + epp.getItem().getId();
        }

        return "redirect:/proyectos/" + epp.getProyecto().getId();
    }

    @GetMapping("/proyectos/{proyectoId}/epp/nuevo")
    public String nuevoEppGlobal(@PathVariable Integer proyectoId, Model model) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        RegistroEpp epp = new RegistroEpp();
        epp.setProyecto(proyecto);
        epp.setItem(null);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("epp", epp);

        return "epp/formulario-global";
    }

    @PostMapping("/proyectos/{proyectoId}/epp/guardar")
    public String guardarEppGlobal(@PathVariable Integer proyectoId,
                                   @ModelAttribute RegistroEpp epp) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        epp.setProyecto(proyecto);
        epp.setItem(null);

        if (epp.getActivo() == null) {
            epp.setActivo(true);
        }

        epp.calcular();
        registroEppRepository.save(epp);

        return "redirect:/proyectos/" + proyectoId;
    }
}