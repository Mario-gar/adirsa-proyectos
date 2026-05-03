package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.RegistroManoObra;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.ProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroManoObraRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.adirsa.gestionproyectos.entity.Proyecto;
import com.adirsa.gestionproyectos.repository.ProyectoRepository;

@Controller
public class RegistroManoObraController {

    private final RegistroManoObraRepository registroManoObraRepository;
    private final ItemProyectoRepository itemProyectoRepository;
    private final ProyectoRepository proyectoRepository;

    public RegistroManoObraController(RegistroManoObraRepository registroManoObraRepository,
                                      ItemProyectoRepository itemProyectoRepository,
                                      ProyectoRepository proyectoRepository) {
        this.registroManoObraRepository = registroManoObraRepository;
        this.itemProyectoRepository = itemProyectoRepository;
        this.proyectoRepository = proyectoRepository;
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
        model.addAttribute("proyecto", manoObra.getProyecto());

        if ("INDIRECTO".equals(manoObra.getTipoCosto())) {
            return "mano-obra/formulario-indirecta";
        }

        model.addAttribute("item", manoObra.getItem());
        return "mano-obra/formulario";
    }

    @PostMapping("/mano-obra/{id}/desactivar")
    public String desactivarManoObra(@PathVariable Integer id) {

        RegistroManoObra manoObra = registroManoObraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de mano de obra no encontrado"));

        manoObra.setActivo(false);
        registroManoObraRepository.save(manoObra);

        if ("INDIRECTO".equals(manoObra.getTipoCosto())) {
            return "redirect:/proyectos/" + manoObra.getProyecto().getId();
        }

        return "redirect:/items/" + manoObra.getItem().getId();
    }

    @GetMapping("/proyectos/{proyectoId}/mano-obra-indirecta/nuevo")
    public String nuevaManoObraIndirecta(@PathVariable Integer proyectoId, Model model) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        RegistroManoObra manoObra = new RegistroManoObra();
        manoObra.setProyecto(proyecto);
        manoObra.setTipoCosto("INDIRECTO");

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("manoObra", manoObra);

        return "mano-obra/formulario-indirecta";
    }

    @PostMapping("/proyectos/{proyectoId}/mano-obra-indirecta/guardar")
    public String guardarManoObraIndirecta(@PathVariable Integer proyectoId,
                                           @ModelAttribute RegistroManoObra manoObra) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        manoObra.setProyecto(proyecto);
        manoObra.setItem(null);
        manoObra.setTipoCosto("INDIRECTO");

        if (manoObra.getActivo() == null) {
            manoObra.setActivo(true);
        }

        manoObra.calcular();
        registroManoObraRepository.save(manoObra);

        return "redirect:/proyectos/" + proyectoId;
    }
}