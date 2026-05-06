package com.adirsa.gestionproyectos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_pendientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoPendiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemProyecto item;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "numero_factura", length = 100)
    private String numeroFactura;

    @Column(length = 150)
    private String proveedor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal cantidad = BigDecimal.ZERO;

    @Column(name = "costo_unitario", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoUnitario = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false, length = 50)
    private String estado = "PENDIENTE";

    @Column(name = "categoria_destino", length = 50)
    private String categoriaDestino;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        preparar();
    }

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
        preparar();
    }

    public void preparar() {

        if (cantidad == null) cantidad = BigDecimal.ZERO;
        if (costoUnitario == null) costoUnitario = BigDecimal.ZERO;

        subtotal = cantidad.multiply(costoUnitario);

        // IVA solo informativo
        if (iva == null) {
            iva = BigDecimal.ZERO;
        }

        // COSTO REAL SIN IVA
        total = subtotal;

        if (estado == null || estado.isBlank()) estado = "PENDIENTE";
        if (activo == null) activo = true;
    }
}