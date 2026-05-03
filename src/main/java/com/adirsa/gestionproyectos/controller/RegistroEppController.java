package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.Proyecto;
import com.adirsa.gestionproyectos.entity.RegistroEpp;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroEppRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroEppController {

    private final RegistroEppRepository registroEppRepository;
    private final ItemProyectoRepository itemProyectoRepository;

    public RegistroEppController(RegistroEppRepository registroEppRepository,
                                 ItemProyectoRepository itemProyectoRepository) {
        this.registroEppRepository = registroEppRepository;
        this.itemProyectoRepository = itemProyectoRepository;
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
        model.addAttribute("item", epp.getItem());
        model.addAttribute("proyecto", epp.getProyecto());

        return "epp/formulario";
    }

    @PostMapping("/epp/{id}/desactivar")
    public String desactivarEpp(@PathVariable Integer id) {

        RegistroEpp epp = registroEppRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EPP no encontrado"));

        epp.setActivo(false);
        registroEppRepository.save(epp);

        return "redirect:/items/" + epp.getItem().getId();
    }
}