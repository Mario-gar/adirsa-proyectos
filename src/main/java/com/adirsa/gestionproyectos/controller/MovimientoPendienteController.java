package com.adirsa.gestionproyectos.controller;

import com.adirsa.gestionproyectos.entity.*;
import com.adirsa.gestionproyectos.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Controller
public class MovimientoPendienteController {

    private final MovimientoPendienteRepository movimientoPendienteRepository;
    private final ProyectoRepository proyectoRepository;
    private final ItemProyectoRepository itemProyectoRepository;
    private final RegistroMaterialRepository registroMaterialRepository;
    private final RegistroEppRepository registroEppRepository;
    private final RegistroTransporteEquipoRepository registroTransporteEquipoRepository;
    private final RegistroCostoIndirectoRepository registroCostoIndirectoRepository;
    private final RegistroSubcontratoRepository registroSubcontratoRepository;

    public MovimientoPendienteController(
            MovimientoPendienteRepository movimientoPendienteRepository,
            ProyectoRepository proyectoRepository,
            ItemProyectoRepository itemProyectoRepository,
            RegistroMaterialRepository registroMaterialRepository,
            RegistroEppRepository registroEppRepository,
            RegistroTransporteEquipoRepository registroTransporteEquipoRepository,
            RegistroCostoIndirectoRepository registroCostoIndirectoRepository,
            RegistroSubcontratoRepository registroSubcontratoRepository) {

        this.movimientoPendienteRepository = movimientoPendienteRepository;
        this.proyectoRepository = proyectoRepository;
        this.itemProyectoRepository = itemProyectoRepository;
        this.registroMaterialRepository = registroMaterialRepository;
        this.registroEppRepository = registroEppRepository;
        this.registroTransporteEquipoRepository = registroTransporteEquipoRepository;
        this.registroCostoIndirectoRepository = registroCostoIndirectoRepository;
        this.registroSubcontratoRepository = registroSubcontratoRepository;
    }

    @GetMapping("/proyectos/{id}/movimientos-pendientes")
    public String verPendientes(@PathVariable Integer id, Model model) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("movimientos",
                movimientoPendienteRepository.findByProyectoIdAndEstadoAndActivoTrue(id, "PENDIENTE"));

        return "movimientos-pendientes/lista";
    }

    @GetMapping("/proyectos/{id}/movimientos-pendientes/nuevo")
    public String nuevoMovimiento(@PathVariable Integer id, Model model) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        MovimientoPendiente movimiento = new MovimientoPendiente();
        movimiento.setProyecto(proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("movimiento", movimiento);

        return "movimientos-pendientes/formulario";
    }

    @PostMapping("/proyectos/{id}/movimientos-pendientes/guardar")
    public String guardarMovimiento(@PathVariable Integer id,
                                    @ModelAttribute MovimientoPendiente movimiento) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        movimiento.setProyecto(proyecto);
        movimiento.setEstado("PENDIENTE");

        if (movimiento.getActivo() == null) {
            movimiento.setActivo(true);
        }

        movimiento.preparar();
        movimientoPendienteRepository.save(movimiento);

        return "redirect:/proyectos/" + id;
    }

    @GetMapping("/proyectos/{id}/movimientos-pendientes/importar")
    public String formularioImportar(@PathVariable Integer id, Model model) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        model.addAttribute("proyecto", proyecto);

        return "movimientos-pendientes/importar";
    }

    @PostMapping("/proyectos/{id}/movimientos-pendientes/importar")
    public String importarExcel(@PathVariable Integer id,
                                @RequestParam("archivo") MultipartFile archivo) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        try (Workbook workbook = WorkbookFactory.create(archivo.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null || filaVacia(row)) {
                    continue;
                }

                MovimientoPendiente movimiento = new MovimientoPendiente();

                movimiento.setProyecto(proyecto);
                movimiento.setFecha(obtenerFecha(row.getCell(0)));
                movimiento.setNumeroFactura(obtenerTexto(row.getCell(1)));
                movimiento.setProveedor(obtenerTexto(row.getCell(2)));
                movimiento.setDescripcion(obtenerTexto(row.getCell(3)));
                movimiento.setUnidadMedida(obtenerTexto(row.getCell(4)));
                movimiento.setCantidad(obtenerNumero(row.getCell(5)));
                movimiento.setCostoUnitario(obtenerNumero(row.getCell(6)));
                movimiento.setSubtotal(obtenerNumero(row.getCell(7)));
                movimiento.setIva(obtenerNumero(row.getCell(8)));
                movimiento.setTotal(movimiento.getSubtotal());

                movimiento.setEstado("PENDIENTE");
                movimiento.setActivo(true);

                movimiento.preparar();
                movimientoPendienteRepository.save(movimiento);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al importar Excel: " + e.getMessage(), e);
        }

        return "redirect:/proyectos/" + id;
    }

    @GetMapping("/movimientos-pendientes/{id}/clasificar")
    public String formularioClasificar(@PathVariable Integer id, Model model) {

        MovimientoPendiente movimiento = movimientoPendienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento pendiente no encontrado"));

        model.addAttribute("movimiento", movimiento);
        model.addAttribute("proyecto", movimiento.getProyecto());
        model.addAttribute("items",
                itemProyectoRepository.findByProyectoIdAndActivoTrue(movimiento.getProyecto().getId()));

        return "movimientos-pendientes/clasificar";
    }

    @PostMapping("/movimientos-pendientes/{id}/clasificar")
    public String clasificar(@PathVariable Integer id,
                             @RequestParam String categoriaDestino,
                             @RequestParam(required = false) Integer itemId,
                             @RequestParam(required = false) String categoriaCostoIndirecto,
                             @RequestParam(required = false) String clasificacionTransporte,
                             @RequestParam(required = false) String estadoSubcontrato) {

        MovimientoPendiente movimiento = movimientoPendienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento pendiente no encontrado"));

        ItemProyecto item = null;

        if (requiereItem(categoriaDestino)) {
            if (itemId == null) {
                throw new RuntimeException("Debe seleccionar un ítem para esta categoría");
            }

            item = itemProyectoRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));
        }

        switch (categoriaDestino) {
            case "MATERIAL" -> crearMaterial(movimiento, item);
            case "EPP_ITEM" -> crearEpp(movimiento, item);
            case "EPP_GLOBAL" -> crearEpp(movimiento, null);
            case "TRANSPORTE_EQUIPO" -> crearTransporteEquipo(movimiento, item, clasificacionTransporte);
            case "COSTO_INDIRECTO" -> crearCostoIndirecto(movimiento, categoriaCostoIndirecto);
            case "SUBCONTRATO" -> crearSubcontrato(movimiento, item, estadoSubcontrato);
            default -> throw new RuntimeException("Categoría no válida");
        }

        movimiento.setCategoriaDestino(categoriaDestino);
        movimiento.setItem(item);
        movimiento.setEstado("ASIGNADO");

        movimientoPendienteRepository.save(movimiento);

        return "redirect:/proyectos/" + movimiento.getProyecto().getId();
    }

    @PostMapping("/movimientos-pendientes/{id}/anular")
    public String anular(@PathVariable Integer id) {

        MovimientoPendiente movimiento = movimientoPendienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento pendiente no encontrado"));

        movimiento.setEstado("ANULADO");
        movimiento.setActivo(false);

        movimientoPendienteRepository.save(movimiento);

        return "redirect:/proyectos/" + movimiento.getProyecto().getId();
    }

    private boolean requiereItem(String categoriaDestino) {
        return categoriaDestino.equals("MATERIAL")
                || categoriaDestino.equals("EPP_ITEM")
                || categoriaDestino.equals("TRANSPORTE_EQUIPO")
                || categoriaDestino.equals("SUBCONTRATO");
    }

    private void crearMaterial(MovimientoPendiente m, ItemProyecto item) {
        RegistroMaterial r = new RegistroMaterial();

        r.setProyecto(m.getProyecto());
        r.setItem(item);
        r.setFecha(m.getFecha());
        r.setProveedor(m.getProveedor());
        r.setTipoCompra("FACTURA");
        r.setNumeroFactura(m.getNumeroFactura());
        r.setDescripcion(m.getDescripcion());
        r.setUnidadMedida(m.getUnidadMedida());
        r.setCantidad(m.getCantidad());
        r.setCostoUnitario(m.getCostoUnitario());
        r.setSubtotal(m.getSubtotal());
        r.setIva(m.getIva());
        r.setTotal(m.getTotal());
        r.setActivo(true);

        registroMaterialRepository.save(r);
    }

    private void crearEpp(MovimientoPendiente m, ItemProyecto item) {
        RegistroEpp r = new RegistroEpp();

        r.setProyecto(m.getProyecto());
        r.setItem(item);
        r.setFecha(m.getFecha());
        r.setVendedor(m.getProveedor());
        r.setNumeroFactura(m.getNumeroFactura());
        r.setDescripcion(m.getDescripcion());
        r.setUnidadMedida(m.getUnidadMedida());
        r.setCantidad(m.getCantidad());
        r.setCostoUnitario(m.getCostoUnitario());
        r.setTotal(m.getTotal());
        r.setActivo(true);

        registroEppRepository.save(r);
    }

    private void crearTransporteEquipo(MovimientoPendiente m, ItemProyecto item, String clasificacion) {
        RegistroTransporteEquipo r = new RegistroTransporteEquipo();

        r.setProyecto(m.getProyecto());
        r.setItem(item);
        r.setFecha(m.getFecha());
        r.setProveedor(m.getProveedor());
        r.setNumeroFactura(m.getNumeroFactura());
        r.setDescripcion(m.getDescripcion());
        r.setUnidadMedida(m.getUnidadMedida());
        r.setCantidad(m.getCantidad());
        r.setCostoUnitario(m.getCostoUnitario());
        r.setTotal(m.getTotal());
        r.setClasificacion(clasificacion);
        r.setActivo(true);

        registroTransporteEquipoRepository.save(r);
    }

    private void crearCostoIndirecto(MovimientoPendiente m, String categoria) {
        RegistroCostoIndirecto r = new RegistroCostoIndirecto();

        r.setProyecto(m.getProyecto());
        r.setFecha(m.getFecha());
        r.setCategoria(categoria);
        r.setProveedor(m.getProveedor());
        r.setNumeroFactura(m.getNumeroFactura());
        r.setDescripcion(m.getDescripcion());
        r.setUnidadMedida(m.getUnidadMedida());
        r.setCantidad(m.getCantidad());
        r.setCostoUnitario(m.getCostoUnitario());
        r.setSubtotal(m.getSubtotal());
        r.setIva(m.getIva());
        r.setTotal(m.getTotal());
        r.setActivo(true);

        registroCostoIndirectoRepository.save(r);
    }

    private void crearSubcontrato(MovimientoPendiente m, ItemProyecto item, String estado) {
        RegistroSubcontrato r = new RegistroSubcontrato();

        r.setProyecto(m.getProyecto());
        r.setItem(item);
        r.setFecha(m.getFecha());
        r.setSubcontratista(m.getProveedor());
        r.setNumeroFactura(m.getNumeroFactura());
        r.setDescripcion(m.getDescripcion());
        r.setMonto(m.getTotal());
        r.setEstado(estado == null || estado.isBlank() ? "PENDIENTE" : estado);
        r.setActivo(true);

        registroSubcontratoRepository.save(r);
    }

    private boolean filaVacia(Row row) {
        for (int i = 0; i <= 8; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !obtenerTexto(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String obtenerTexto(Cell cell) {
        if (cell == null) return "";

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            double valor = cell.getNumericCellValue();

            if (valor == Math.floor(valor)) {
                return String.valueOf((long) valor);
            }

            return String.valueOf(valor);
        }

        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getStringCellValue().trim();
            } catch (Exception e) {
                return String.valueOf(cell.getNumericCellValue());
            }
        }

        return "";
    }

    private BigDecimal obtenerNumero(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;

        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }

        if (cell.getCellType() == CellType.STRING) {
            try {
                String valor = cell.getStringCellValue()
                        .replace("C$", "")
                        .replace("C", "")
                        .replace("$", "")
                        .replace(",", "")
                        .trim();

                if (valor.isBlank()) return BigDecimal.ZERO;

                return new BigDecimal(valor);
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }

        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }

        return BigDecimal.ZERO;
    }

    private LocalDate obtenerFecha(Cell cell) {
        if (cell == null) return LocalDate.now();

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        return LocalDate.now();
    }
}