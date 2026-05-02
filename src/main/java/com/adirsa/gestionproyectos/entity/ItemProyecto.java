package com.adirsa.gestionproyectos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items_proyecto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(length = 50)
    private String codigo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;

    @Column(name = "cantidad_presupuestada", nullable = false, precision = 14, scale = 2)
    private BigDecimal cantidadPresupuestada = BigDecimal.ZERO;

    @Column(name = "precio_unitario_presupuestado", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioUnitarioPresupuestado = BigDecimal.ZERO;

    @Column(name = "total_presupuestado", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPresupuestado = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}