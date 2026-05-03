package com.adirsa.gestionproyectos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_materiales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemProyecto item;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 150)
    private String proveedor;

    @Column(name = "tipo_compra", length = 50)
    private String tipoCompra;

    @Column(name = "numero_factura", length = 100)
    private String numeroFactura;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;

    @Column(precision = 14, scale = 2)
    private BigDecimal cantidad = BigDecimal.ZERO;

    @Column(name = "costo_unitario", precision = 14, scale = 2)
    private BigDecimal costoUnitario = BigDecimal.ZERO;

    @Column(precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 14, scale = 2)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        calcular();
    }

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
        calcular();
    }

    public void calcular() {
        if (cantidad == null) cantidad = BigDecimal.ZERO;
        if (costoUnitario == null) costoUnitario = BigDecimal.ZERO;
        if (iva == null) iva = BigDecimal.ZERO;
        if (activo == null) activo = true;

        subtotal = cantidad.multiply(costoUnitario);
        total = subtotal.add(iva);
    }
}