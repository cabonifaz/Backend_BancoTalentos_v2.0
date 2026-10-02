package com.bdt.bancotalentosbackend.model.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lo que se pudo leer de un FMI (FT-GTH-12, Formulario de Ingreso).
 *
 * El formulario NO identifica a una persona: trae su nombre y los datos del
 * puesto, pero ni DNI, ni correo, ni celular. Por eso lo que viaja aquí sirve
 * para dos cosas y ninguna más: proponer a quién buscar en el banco de talentos
 * y dar contexto en pantalla. Nada de esto se persiste.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FMIExtractionDTO {

    /**
     * Falso cuando el PDF no es un FT-GTH-12 o es de movimiento/cese. El
     * frontend corta el flujo y muestra {@link #motivoDescarte}.
     */
    private boolean esFormularioIngreso;
    private String motivoDescarte;

    /** ALTA, MEDIA o BAJA: qué tan seguro es que se leyó bien. */
    private String confianza;

    /** PARSER (etiquetas), IA (respaldo) o MIXTO. */
    private String origen;

    // ─── Colaborador ────────────────────────────────────────────────────────

    /** Tal cual está en la celda del formulario, en una sola línea. */
    private String nombreCompleto;

    /**
     * Partición sugerida de {@link #nombreCompleto}. El formulario no garantiza
     * el orden ni separa apellidos, así que es una heurística: el frontend la
     * muestra siempre editable y nunca la da por buena.
     */
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;

    /** "Cliente" cuando el equipo es Outsourcing, "Equipo" en el resto. */
    private String etiquetaEquipo;
    private String equipoOCliente;
    private boolean esOutsourcing;

    // ─── Ingreso ────────────────────────────────────────────────────────────

    private String modalidad;
    private String motivoIngreso;
    private String cargo;
    private String horario;

    /**
     * Ya sin símbolo de moneda ni separador de miles. El bono no se lee: la
     * plantilla tiene la celda, pero AutFMI nunca la llena, así que siempre
     * saldría vacía.
     */
    private BigDecimal montoBase;
    private BigDecimal montoMovilidad;

    /** yyyy-MM-dd; en el PDF vienen como dd/MM/yyyy. */
    private String fechaInicioContrato;
    private String fechaFinContrato;

    private String proyectoServicio;
    private String objetoContrato;
    private String declaraSunat;
    private String sedeDeclarar;

    // ─── Pie ────────────────────────────────────────────────────────────────

    private String gestor;
    private String fechaEmision;

    /** Campos que el formulario traía en blanco, para avisarlo en pantalla. */
    private List<String> camposFaltantes = new ArrayList<>();
}
