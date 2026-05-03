package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.RegistroManoObra;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroManoObraRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroManoObraController {

    private final RegistroManoObraRepository registroManoObraRepository;
    private final ItemProyectoRepository itemProyectoRepository;

    public RegistroManoObraController(RegistroManoObraRepository registroManoObraRepository,
                                      ItemProyectoRepository itemProyectoRepository) {
        this.registroManoObraRepository = registroManoObraRepository;
        this.itemProyectoRepository = itemProyectoRepository;
    }

    @GetMapping("/items/{itemId}/mano-obra/nuevo")
    public String nuevaManoObra(@PathVariable Integer itemId, Model model) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        RegistroManoObra manoObra = new RegistroManoObra();
        manoObra.setItem(item);
        manoObra.setProyecto(item.getProyecto());

        model.addAttribute("item", item);
        model.addAttribute("proyecto", item.getProyecto());
        model.addAttribute("manoObra", manoObra);

        return "mano-obra/formulario";
    }

    @PostMapping("/items/{itemId}/mano-obra/guardar")
    public String guardarManoObra(@PathVariable Integer itemId,
                                  @ModelAttribute RegistroManoObra manoObra) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        manoObra.setItem(item);
        manoObra.setProyecto(item.getProyecto());
        manoObra.setTipoCosto("DIRECTO");

        if (manoObra.getActivo() == null) {
            manoObra.setActivo(true);
        }

        manoObra.calcular();
        registroManoObraRepository.save(manoObra);

        return "redirect:/items/" + itemId;
    }

    @GetMapping("/mano-obra/{id}/editar")
    public String editarManoObra(@PathVariable Integer id, Model model) {

        RegistroManoObra manoObra = registroManoObraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de mano de obra no encontrado"));

        model.addAttribute("manoObra", manoObra);
        model.addAttribute("item", manoObra.getItem());
        model.addAttribute("proyecto", manoObra.getProyecto());

        return "mano-obra/formulario";
    }

    @PostMapping("/mano-obra/{id}/desactivar")
    public String desactivarManoObra(@PathVariable Integer id) {

        RegistroManoObra manoObra = registroManoObraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de mano de obra no encontrado"));

        manoObra.setActivo(false);
        registroManoObraRepository.save(manoObra);

        return "redirect:/items/" + manoObra.getItem().getId();
    }
}