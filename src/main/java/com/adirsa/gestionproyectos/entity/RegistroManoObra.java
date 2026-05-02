package com.adirsa.gestionproyectos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_mano_obra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroManoObra {

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

    @Column(length = 100)
    private String periodo;

    @Column(name = "numero_planilla", length = 100)
    private String numeroPlanilla;

    @Column(nullable = false, length = 150)
    private String empleado;

    @Column(length = 100)
    private String cargo;

    @Column(name = "dias_trabajados", precision = 14, scale = 2)
    private BigDecimal diasTrabajados = BigDecimal.ZERO;

    @Column(name = "pago_por_dia", precision = 14, scale = 2)
    private BigDecimal pagoPorDia = BigDecimal.ZERO;

    @Column(name = "salario_basico", precision = 14, scale = 2)
    private BigDecimal salarioBasico = BigDecimal.ZERO;

    @Column(precision = 14, scale = 2)
    private BigDecimal incentivos = BigDecimal.ZERO;

    @Column(precision = 14, scale = 2)
    private BigDecimal viaticos = BigDecimal.ZERO;

    @Column(name = "salario_bruto", precision = 14, scale = 2)
    private BigDecimal salarioBruto = BigDecimal.ZERO;

    @Column(precision = 14, scale = 2)
    private BigDecimal prestamos = BigDecimal.ZERO;

    @Column(name = "salario_neto", precision = 14, scale = 2)
    private BigDecimal salarioNeto = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}