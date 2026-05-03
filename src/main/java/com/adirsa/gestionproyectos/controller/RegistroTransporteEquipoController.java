package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.RegistroTransporteEquipo;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroTransporteEquipoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroTransporteEquipoController {

    private final RegistroTransporteEquipoRepository registroTransporteEquipoRepository;
    private final ItemProyectoRepository itemProyectoRepository;

    public RegistroTransporteEquipoController(
            RegistroTransporteEquipoRepository registroTransporteEquipoRepository,
            ItemProyectoRepository itemProyectoRepository) {
        this.registroTransporteEquipoRepository = registroTransporteEquipoRepository;
        this.itemProyectoRepository = itemProyectoRepository;
    }

    @GetMapping("/items/{itemId}/transporte-equipo/nuevo")
    public String nuevoTransporteEquipo(@PathVariable Integer itemId, Model model) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        RegistroTransporteEquipo transporteEquipo = new RegistroTransporteEquipo();
        transporteEquipo.setItem(item);
        transporteEquipo.setProyecto(item.getProyecto());

        model.addAttribute("item", item);
        model.addAttribute("proyecto", item.getProyecto());
        model.addAttribute("transporteEquipo", transporteEquipo);

        return "transporte-equipo/formulario";
    }

    @PostMapping("/items/{itemId}/transporte-equipo/guardar")
    public String guardarTransporteEquipo(@PathVariable Integer itemId,
                                          @ModelAttribute RegistroTransporteEquipo transporteEquipo) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        transporteEquipo.setItem(item);
        transporteEquipo.setProyecto(item.getProyecto());

        if (transporteEquipo.getActivo() == null) {
            transporteEquipo.setActivo(true);
        }

        transporteEquipo.calcular();
        registroTransporteEquipoRepository.save(transporteEquipo);

        return "redirect:/items/" + itemId;
    }

    @GetMapping("/transporte-equipo/{id}/editar")
    public String editarTransporteEquipo(@PathVariable Integer id, Model model) {

        RegistroTransporteEquipo transporteEquipo = registroTransporteEquipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de transporte/equipo no encontrado"));

        model.addAttribute("transporteEquipo", transporteEquipo);
        model.addAttribute("item", transporteEquipo.getItem());
        model.addAttribute("proyecto", transporteEquipo.getProyecto());

        return "transporte-equipo/formulario";
    }

    @PostMapping("/transporte-equipo/{id}/desactivar")
    public String desactivarTransporteEquipo(@PathVariable Integer id) {

        RegistroTransporteEquipo transporteEquipo = registroTransporteEquipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de transporte/equipo no encontrado"));

        transporteEquipo.setActivo(false);
        registroTransporteEquipoRepository.save(transporteEquipo);

        return "redirect:/items/" + transporteEquipo.getItem().getId();
    }
}