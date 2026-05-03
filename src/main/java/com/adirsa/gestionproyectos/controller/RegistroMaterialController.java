package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.ItemProyecto;
import com.adirsa.gestionproyectos.entity.RegistroMaterial;
import com.adirsa.gestionproyectos.repository.ItemProyectoRepository;
import com.adirsa.gestionproyectos.repository.RegistroMaterialRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroMaterialController {

    private final RegistroMaterialRepository registroMaterialRepository;
    private final ItemProyectoRepository itemProyectoRepository;

    public RegistroMaterialController(RegistroMaterialRepository registroMaterialRepository,
                                      ItemProyectoRepository itemProyectoRepository) {
        this.registroMaterialRepository = registroMaterialRepository;
        this.itemProyectoRepository = itemProyectoRepository;
    }

    @GetMapping("/items/{itemId}/materiales/nuevo")
    public String nuevoMaterial(@PathVariable Integer itemId, Model model) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        RegistroMaterial material = new RegistroMaterial();
        material.setItem(item);
        material.setProyecto(item.getProyecto());

        model.addAttribute("item", item);
        model.addAttribute("proyecto", item.getProyecto());
        model.addAttribute("material", material);

        return "materiales/formulario";
    }

    @PostMapping("/items/{itemId}/materiales/guardar")
    public String guardarMaterial(@PathVariable Integer itemId,
                                  @ModelAttribute RegistroMaterial material) {

        ItemProyecto item = itemProyectoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        material.setItem(item);
        material.setProyecto(item.getProyecto());

        if (material.getActivo() == null) {
            material.setActivo(true);
        }

        material.calcular();
        registroMaterialRepository.save(material);

        return "redirect:/items/" + itemId;
    }

    @GetMapping("/materiales/{id}/editar")
    public String editarMaterial(@PathVariable Integer id, Model model) {

        RegistroMaterial material = registroMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));

        model.addAttribute("material", material);
        model.addAttribute("item", material.getItem());
        model.addAttribute("proyecto", material.getProyecto());

        return "materiales/formulario";
    }

    @PostMapping("/materiales/{id}/desactivar")
    public String desactivarMaterial(@PathVariable Integer id) {

        RegistroMaterial material = registroMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));

        material.setActivo(false);
        registroMaterialRepository.save(material);

        return "redirect:/items/" + material.getItem().getId();
    }
}