package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.RegistroSubcontrato;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroSubcontratoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroSubcontratoController {

    private final RegistroSubcontratoRepository repository;
    private final ItemProyectoRepository itemProyectoRepository;

    public RegistroSubcontratoController(RegistroSubcontratoRepository repository,
                                         ItemProyectoRepository itemProyectoRepository) {
        this.repository = repository;
        this.itemProyectoRepository = itemProyectoRepository;
    }

    @GetMapping("/items/{itemId}/subcontratos/nuevo")
    public String nuevo(@PathVariable Integer itemId, Model model) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        RegistroSubcontrato s = new RegistroSubcontrato();
        s.setItem(item);
        s.setProyecto(item.getProyecto());

        model.addAttribute("item", item);
        model.addAttribute("proyecto", item.getProyecto());
        model.addAttribute("subcontrato", s);

        return "subcontratos/formulario";
    }

    @PostMapping("/items/{itemId}/subcontratos/guardar")
    public String guardar(@PathVariable Integer itemId,
                          @ModelAttribute RegistroSubcontrato subcontrato) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        subcontrato.setItem(item);
        subcontrato.setProyecto(item.getProyecto());

        if (subcontrato.getActivo() == null) {
            subcontrato.setActivo(true);
        }

        subcontrato.preparar();
        repository.save(subcontrato);

        return "redirect:/items/" + itemId;
    }

    @GetMapping("/subcontratos/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {

        RegistroSubcontrato s = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcontrato no encontrado"));

        model.addAttribute("subcontrato", s);
        model.addAttribute("item", s.getItem());
        model.addAttribute("proyecto", s.getProyecto());

        return "subcontratos/formulario";
    }

    @PostMapping("/subcontratos/{id}/desactivar")
    public String desactivar(@PathVariable Integer id) {

        RegistroSubcontrato s = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcontrato no encontrado"));

        s.setActivo(false);
        repository.save(s);

        return "redirect:/items/" + s.getItem().getId();
    }
}