package com.bdt.bancotalentosbackend.util;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bdt.bancotalentosbackend.model.response.FMIExtractionDTO;

/**
 * Lee un FMI (FT-GTH-12) del texto plano del PDF, por etiquetas.
 *
 * El formulario lo genera AutFMI desde una plantilla fija
 * (`formulario_movimiento.html`), así que las etiquetas son literales conocidos
 * y un parser acierta sin gastar una llamada a la IA. Cuando el PDF viene
 * redactado fuera y el orden de lectura no cuadra, el servicio se cae al
 * análisis con IA; por eso aquí nunca se inventa nada: lo que no se encuentra
 * se devuelve en null y se anota en `camposFaltantes`.
 *
 * Detalle que condiciona todo: la plantilla es la misma para ingreso,
 * movimiento y cese, de modo que **el PDF siempre trae las tres secciones** y
 * el título siempre dice "FORMULARIO DE MOVIMIENTO". El tipo se deduce de qué
 * sección trae valores, nunca del encabezado.
 */
public final class FmiTextParser {

    private FmiTextParser() {
    }

    // ─── Etiquetas de la plantilla ──────────────────────────────────────────

    private static final String L_NOMBRE = "Nombres y Apellidos";
    private static final String L_EQUIPO = "Equipo";
    private static final String L_CLIENTE = "Cliente";
    private static final String L_MODALIDAD = "Modalidad";
    private static final String L_MOTIVO = "Motivo de Ingreso";
    private static final String L_CARGO = "Cargo";
    private static final String L_HORARIO = "Horario de Trabajo";
    private static final String L_FCH_INICIO = "F. Inicio contrato";
    private static final String L_FCH_FIN = "F. Termino contrato";
    private static final String L_PROYECTO = "Proyecto / servicio";
    private static final String L_OBJETO = "Objeto del contrato";
    private static final String L_SUNAT = "[1] Declarado en SUNAT?";
    private static final String L_SEDE = "[2] Sede a declarar";
    private static final String L_ESTRUCTURA = "Estructura Salarial";
    private static final String L_GESTOR = "Gestor de Servicios";
    private static final String L_EMISION = "Fecha de emision:";

    /** Sólo se usan para decidir el tipo de formulario. */
    private static final List<String> L_MOVIMIENTO = Arrays.asList(
            "Cambio de puesto", "Cambio de area", "Cambio de jornada", "F. Inicio de movimiento");
    private static final List<String> L_CESE = Arrays.asList(
            "Motivo de Cese", "Fecha de Cese", "Fecha de devolucion Equipo");

    /** Títulos de sección: marcan dónde termina el valor de una etiqueta. */
    private static final List<String> SECCIONES = Arrays.asList(
            "DATOS DEL COLABORADOR", "INGRESO", "DECLARACION EN SUNAT", "MOVIMIENTO", "CESE");

    /**
     * Textos de relleno de la plantilla. En un PDF generado nunca aparecen
     * (Thymeleaf los reemplaza), pero un FMI redactado a mano sobre una copia
     * puede dejarlos, y valen tanto como una celda vacía.
     */
    private static final List<String> PLACEHOLDERS = Arrays.asList(
            "Escribir el nombre del colaborador.", "Escribir el cargo.", "Escoja una fecha.",
            "Elija un motivo.", "Monto", "Nombre del encargado", "Sede a declarar");

    /** Marcadores que debe tener cualquier FT-GTH-12 para considerarse tal. */
    private static final List<String> MARCADORES = Arrays.asList(
            "FORMULARIO DE MOVIMIENTO", "DATOS DEL COLABORADOR", L_NOMBRE, "INGRESO");

    /** Partículas que pertenecen al apellido que les sigue. */
    private static final Set<String> PARTICULAS = new LinkedHashSet<>(Arrays.asList(
            "DE", "DEL", "LA", "LAS", "LOS", "DA", "DAS", "DO", "DOS", "DI", "VAN", "VON", "MC", "SAN"));

    private static final String AREA_OUTSOURCING = "OUTSOURCING";

    // ─── API ────────────────────────────────────────────────────────────────

    /**
     * ¿El texto corresponde a un FT-GTH-12? Se exige la mayoría de los
     * marcadores: con uno suelto podría ser cualquier documento que mencione
     * "INGRESO".
     */
    public static boolean pareceFormulario(String texto) {
        if (texto == null || texto.isBlank())
            return false;

        String plano = normalizar(texto);
        long encontrados = MARCADORES.stream()
                .filter(marcador -> plano.contains(normalizar(marcador)))
                .count();

        return encontrados >= MARCADORES.size() - 1;
    }

    /**
     * Extrae el formulario. Devuelve siempre un DTO: si el PDF resulta ser de
     * movimiento o de cese, viene con `esFormularioIngreso = false` y el motivo.
     */
    public static FMIExtractionDTO parse(String texto, String nombreArchivo) {
        List<String> lineas = lineas(texto);

        FMIExtractionDTO dto = new FMIExtractionDTO();
        dto.setOrigen("PARSER");

        dto.setNombreCompleto(valor(lineas, L_NOMBRE));
        partirNombre(dto);

        // La fila 2 del bloque de colaborador cambia de etiqueta según el
        // equipo: dice "Cliente" en outsourcing y "Equipo" en el resto.
        String cliente = valor(lineas, L_CLIENTE);
        String equipo = valor(lineas, L_EQUIPO);
        dto.setEsOutsourcing(cliente != null || esOutsourcing(equipo));
        dto.setEtiquetaEquipo(cliente != null ? L_CLIENTE : L_EQUIPO);
        dto.setEquipoOCliente(cliente != null ? cliente : equipo);

        dto.setModalidad(valor(lineas, L_MODALIDAD));
        dto.setMotivoIngreso(valor(lineas, L_MOTIVO));
        dto.setCargo(valor(lineas, L_CARGO));
        dto.setHorario(valor(lineas, L_HORARIO));
        dto.setFechaInicioContrato(aIso(valor(lineas, L_FCH_INICIO)));
        dto.setFechaFinContrato(aIso(valor(lineas, L_FCH_FIN)));
        dto.setProyectoServicio(valor(lineas, L_PROYECTO));
        dto.setObjetoContrato(valor(lineas, L_OBJETO));
        dto.setDeclaraSunat(valor(lineas, L_SUNAT));
        dto.setSedeDeclarar(valor(lineas, L_SEDE));
        dto.setGestor(gestor(lineas));
        dto.setFechaEmision(fechaEmision(lineas));

        montos(lineas, dto);

        clasificar(lineas, dto, nombreArchivo);
        dto.setCamposFaltantes(faltantes(dto));

        return dto;
    }

    // ─── Tipo de formulario ─────────────────────────────────────────────────

    /**
     * Ingreso, movimiento o cese: se decide contando qué bloque trae valores,
     * porque el encabezado del PDF dice siempre "FORMULARIO DE MOVIMIENTO".
     */
    private static void clasificar(List<String> lineas, FMIExtractionDTO dto, String nombreArchivo) {
        int ingreso = contarConValor(dto.getModalidad(), dto.getMotivoIngreso(), dto.getCargo(),
                dto.getHorario(), dto.getFechaInicioContrato());
        int movimiento = (int) L_MOVIMIENTO.stream().filter(l -> valor(lineas, l) != null).count();
        int cese = (int) L_CESE.stream().filter(l -> valor(lineas, l) != null).count();

        if (ingreso == 0) {
            dto.setEsFormularioIngreso(false);
            dto.setConfianza("ALTA");
            dto.setMotivoDescarte(movimiento > 0 || cese > 0
                    ? "El PDF es un formulario de " + (cese > 0 ? "cese" : "movimiento") + ", no de ingreso."
                    : "El formulario no tiene datos de ingreso.");
            return;
        }

        dto.setEsFormularioIngreso(true);

        // Un ingreso limpio no trae nada en movimiento ni en cese. Si trae
        // ambas cosas, se acepta pero se avisa: puede ser un formulario
        // reutilizado a mano.
        boolean mezclado = movimiento > 0 || cese > 0;
        boolean archivoLoConfirma = nombreArchivo != null
                && normalizar(nombreArchivo).contains(normalizar("Formulario de Ingreso"));

        if (mezclado)
            dto.setConfianza("MEDIA");
        else if (ingreso >= 4 || archivoLoConfirma)
            dto.setConfianza("ALTA");
        else
            dto.setConfianza("MEDIA");
    }

    private static int contarConValor(String... valores) {
        return (int) Arrays.stream(valores).filter(v -> v != null && !v.isBlank()).count();
    }

    private static List<String> faltantes(FMIExtractionDTO dto) {
        List<String> faltan = new ArrayList<>();
        if (dto.getNombreCompleto() == null)
            faltan.add("Nombres y Apellidos");
        if (dto.getEquipoOCliente() == null)
            faltan.add("Equipo/Cliente");
        if (dto.getCargo() == null)
            faltan.add("Cargo");
        if (dto.getModalidad() == null)
            faltan.add("Modalidad");
        if (dto.getFechaInicioContrato() == null)
            faltan.add("F. Inicio contrato");
        if (dto.getMontoBase() == null)
            faltan.add("Estructura salarial");
        return faltan;
    }

    // ─── Lectura por etiquetas ──────────────────────────────────────────────

    private static List<String> lineas(String texto) {
        List<String> lineas = new ArrayList<>();
        for (String linea : texto.replace("\r\n", "\n").replace('\r', '\n').split("\n")) {
            String limpia = linea.trim();
            if (!limpia.isEmpty())
                lineas.add(limpia);
        }
        return lineas;
    }

    /**
     * Valor de una etiqueta. La celda puede quedar en la misma línea que su
     * rótulo o en la siguiente, según cómo ordene PDFBox la tabla, así que se
     * miran ambas y se corta en cuanto aparece otra etiqueta o un título de
     * sección.
     */
    private static String valor(List<String> lineas, String etiqueta) {
        String porLinea = valorPorLinea(lineas, etiqueta);
        if (porLinea != null)
            return porLinea;

        // Segunda pasada: la columna de rótulos es estrecha, así que una
        // etiqueta larga ("Estructura Salarial", "Horario de Trabajo") se parte
        // en dos líneas y nunca empieza una. Buscarla sobre el texto aplanado
        // la encuentra igual.
        return valorAplanado(lineas, etiqueta);
    }

    /** Busca la etiqueta al inicio de una línea, que es el caso normal. */
    private static String valorPorLinea(List<String> lineas, String etiqueta) {
        String objetivo = normalizar(etiqueta);

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (!normalizar(linea).startsWith(objetivo))
                continue;

            // El corte se busca sobre la línea original: normalizar colapsa los
            // espacios repetidos que mete el PDF, así que la longitud de la
            // etiqueta no sirve como índice.
            int corte = finDeEtiqueta(linea, objetivo);
            String resto = corte < 0 ? "" : linea.substring(corte).trim();
            resto = limpiarValor(resto);
            if (resto != null)
                return resto;

            // Rótulo solo en su línea: el valor está en la siguiente que no sea
            // otra etiqueta.
            for (int j = i + 1; j < lineas.size() && j <= i + 2; j++) {
                String candidato = limpiarValor(lineas.get(j));
                if (candidato == null)
                    continue;
                if (esEtiquetaOSeccion(candidato))
                    return null;
                return candidato;
            }
            return null;
        }
        return null;
    }

    /** Todas las etiquetas de la plantilla, para saber dónde acaba un valor. */
    private static final List<String> TODAS_LAS_ETIQUETAS = Arrays.asList(
            L_NOMBRE, L_EQUIPO, L_CLIENTE, L_MODALIDAD, L_MOTIVO, L_CARGO, L_HORARIO,
            L_FCH_INICIO, L_FCH_FIN, L_PROYECTO, L_OBJETO, L_SUNAT, L_SEDE, L_ESTRUCTURA,
            L_GESTOR, L_EMISION);

    /**
     * El texto en una sola línea, con los espacios ya colapsados.
     *
     * Es lo que permite encontrar una etiqueta que el PDF partió en dos líneas
     * porque su celda es estrecha.
     */
    private static String aplanar(List<String> lineas) {
        return String.join(" ", lineas).replaceAll("\\s+", " ").trim();
    }

    /**
     * Versión comparable del texto que CONSERVA la longitud, para poder cortar
     * el original por el mismo índice: mayúsculas, sin tildes y con los signos
     * de apertura convertidos en espacio en vez de eliminados.
     */
    private static String clave(String texto) {
        String descompuesto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        StringBuilder salida = new StringBuilder(descompuesto.length());
        for (char caracter : descompuesto.toCharArray()) {
            if (Character.getType(caracter) == Character.NON_SPACING_MARK)
                continue;
            salida.append(caracter == '¿' || caracter == '¡' ? ' ' : Character.toUpperCase(caracter));
        }
        return salida.toString();
    }

    /**
     * Valor de una etiqueta sobre el texto aplanado: va desde el final del
     * rótulo hasta la siguiente etiqueta o título de sección que aparezca.
     */
    private static String valorAplanado(List<String> lineas, String etiqueta) {
        String plano = aplanar(lineas);
        String planoClave = clave(plano);

        int inicio = planoClave.indexOf(clave(etiqueta));
        if (inicio < 0)
            return null;

        int desde = inicio + etiqueta.length();
        if (desde >= plano.length())
            return null;

        int hasta = plano.length();
        for (String otra : TODAS_LAS_ETIQUETAS) {
            if (otra.equalsIgnoreCase(etiqueta))
                continue;
            int posicion = planoClave.indexOf(clave(otra), desde);
            if (posicion >= 0 && posicion < hasta)
                hasta = posicion;
        }
        for (String seccion : SECCIONES) {
            int posicion = planoClave.indexOf(clave(seccion), desde);
            if (posicion >= 0 && posicion < hasta)
                hasta = posicion;
        }

        return limpiarValor(plano.substring(desde, hasta));
    }

    /**
     * El pie no sigue el patrón etiqueta→valor: el nombre del gestor se imprime
     * encima del rótulo "Gestor de Servicios", como en una firma.
     */
    private static String gestor(List<String> lineas) {
        String objetivo = normalizar(L_GESTOR);

        for (int i = 1; i < lineas.size(); i++) {
            if (!normalizar(lineas.get(i)).startsWith(objetivo))
                continue;

            String anterior = limpiarValor(lineas.get(i - 1));
            if (anterior != null && !esEtiquetaOSeccion(anterior))
                return anterior;
        }
        return null;
    }

    /** Va dentro de la línea de pie, no al principio: "… Fecha de emisión: X". */
    private static String fechaEmision(List<String> lineas) {
        String objetivo = normalizar(L_EMISION);

        for (String linea : lineas) {
            int posicion = normalizar(linea).indexOf(objetivo);
            if (posicion < 0)
                continue;

            int corte = linea.lastIndexOf(':');
            if (corte >= 0 && corte + 1 < linea.length())
                return limpiarValor(linea.substring(corte + 1));
        }
        return null;
    }

    /**
     * Posición en la línea original donde termina la etiqueta, comparando
     * prefijo a prefijo ya normalizado. Devuelve -1 si no está.
     */
    private static int finDeEtiqueta(String linea, String etiquetaNormalizada) {
        for (int i = 1; i <= linea.length(); i++) {
            if (normalizar(linea.substring(0, i)).equals(etiquetaNormalizada))
                return i;
        }
        return -1;
    }

    /** Descarta celdas vacías, rellenos de la plantilla y restos de bordes. */
    private static String limpiarValor(String bruto) {
        if (bruto == null)
            return null;

        String limpio = bruto.replaceAll("^[:\\-–|\\s]+", "").trim();
        if (limpio.isEmpty())
            return null;

        for (String placeholder : PLACEHOLDERS) {
            if (normalizar(limpio).equals(normalizar(placeholder)))
                return null;
        }
        return limpio;
    }

    private static boolean esEtiquetaOSeccion(String texto) {
        String plano = normalizar(texto);

        for (String seccion : SECCIONES) {
            if (plano.startsWith(normalizar(seccion)))
                return true;
        }
        for (String etiqueta : Arrays.asList(L_NOMBRE, L_EQUIPO, L_CLIENTE, L_MODALIDAD, L_MOTIVO,
                L_CARGO, L_HORARIO, L_FCH_INICIO, L_FCH_FIN, L_PROYECTO, L_OBJETO, L_SUNAT, L_SEDE,
                L_ESTRUCTURA, L_GESTOR, L_EMISION)) {
            if (plano.startsWith(normalizar(etiqueta)))
                return true;
        }
        return false;
    }

    // ─── Montos ─────────────────────────────────────────────────────────────

    /**
     * La estructura salarial no tiene rótulo propio por importe: es una fila de
     * celdas numéricas debajo de los checks Base / Movilidad / Bono. Se buscan
     * los importes en el tramo que va de "Estructura Salarial" a la siguiente
     * etiqueta, y se asignan en el orden de la plantilla: base y movilidad. El
     * bono tiene celda pero AutFMI nunca la llena.
     *
     * Se trabaja sobre el texto aplanado a propósito: "Estructura Salarial" vive
     * en una columna estrecha y el PDF la parte en dos líneas, así que buscarla
     * al inicio de una línea no la encontraba y los montos salían vacíos.
     */
    private static void montos(List<String> lineas, FMIExtractionDTO dto) {
        String plano = aplanar(lineas);
        String planoClave = clave(plano);

        int inicio = planoClave.indexOf(clave(L_ESTRUCTURA));
        if (inicio < 0)
            return;

        int desde = inicio + L_ESTRUCTURA.length();
        int hasta = plano.length();
        for (String corte : Arrays.asList(L_FCH_INICIO, L_FCH_FIN, L_PROYECTO, L_OBJETO,
                "DECLARACION EN SUNAT", "MOVIMIENTO")) {
            int posicion = planoClave.indexOf(clave(corte), desde);
            if (posicion >= 0 && posicion < hasta)
                hasta = posicion;
        }
        if (desde >= hasta)
            return;

        List<BigDecimal> importes = new ArrayList<>();
        Matcher busqueda = IMPORTE.matcher(plano.substring(desde, hasta));
        while (busqueda.find() && importes.size() < 2) {
            BigDecimal monto = aMonto(busqueda.group());
            if (monto != null)
                importes.add(monto);
        }

        if (!importes.isEmpty())
            dto.setMontoBase(importes.get(0));
        if (importes.size() > 1)
            dto.setMontoMovilidad(importes.get(1));
    }

    /**
     * Un importe del formulario: "3,500.00", "3500.00" o "1600". Exige dos
     * decimales o ausencia de separadores para no confundir con una fecha o con
     * el número de versión de la plantilla.
     */
    private static final Pattern IMPORTE = Pattern.compile(
            "\\d{1,3}(?:,\\d{3})+(?:\\.\\d{2})?|\\d+\\.\\d{2}|\\b\\d{3,7}\\b");

    /** "S/ 3,500.00" o "3500.00" → 3500.00; cualquier otra cosa → null. */
    private static BigDecimal aMonto(String bruto) {
        if (bruto == null)
            return null;

        String limpio = bruto.replaceAll("[^0-9.,]", "").replace(",", "");
        if (limpio.isEmpty() || limpio.equals(".") || !limpio.matches("\\d+(\\.\\d+)?"))
            return null;

        try {
            return new BigDecimal(limpio);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ─── Fechas y nombre ────────────────────────────────────────────────────

    /** dd/MM/yyyy (lo que imprime el formulario) → yyyy-MM-dd. */
    private static String aIso(String bruto) {
        if (bruto == null)
            return null;

        String limpio = bruto.trim();
        if (!limpio.matches("\\d{1,2}/\\d{1,2}/\\d{4}.*"))
            return null;

        String[] partes = limpio.split("[^0-9]");
        return String.format("%s-%02d-%02d",
                partes[2], Integer.parseInt(partes[1]), Integer.parseInt(partes[0]));
    }

    /**
     * El formulario imprime "Nombres y Apellidos" en una sola celda, con el
     * formato nombres + apellidos que arma AutFMI. Se parte por ahí, pegando
     * las partículas al apellido que les sigue, pero es una sugerencia: el
     * frontend muestra los tres campos editables porque un FMI externo puede
     * traerlos al revés.
     */
    private static void partirNombre(FMIExtractionDTO dto) {
        String completo = dto.getNombreCompleto();
        if (completo == null)
            return;

        List<String> tokens = new ArrayList<>();
        for (String token : completo.trim().split("\\s+")) {
            if (!token.isBlank())
                tokens.add(token);
        }
        if (tokens.isEmpty())
            return;

        // Las partículas se unen al token siguiente ("de la Cruz" → "de la Cruz").
        List<String> bloques = new ArrayList<>();
        StringBuilder pendiente = new StringBuilder();
        for (String token : tokens) {
            if (PARTICULAS.contains(normalizar(token))) {
                pendiente.append(pendiente.length() == 0 ? "" : " ").append(token);
                continue;
            }
            if (pendiente.length() > 0) {
                bloques.add(pendiente + " " + token);
                pendiente.setLength(0);
            } else {
                bloques.add(token);
            }
        }
        if (pendiente.length() > 0)
            bloques.add(pendiente.toString());

        if (bloques.size() == 1) {
            dto.setNombres(bloques.get(0));
            return;
        }
        if (bloques.size() == 2) {
            dto.setNombres(bloques.get(0));
            dto.setApellidoPaterno(bloques.get(1));
            return;
        }

        int corte = bloques.size() - 2;
        dto.setNombres(String.join(" ", bloques.subList(0, corte)));
        dto.setApellidoPaterno(bloques.get(corte));
        dto.setApellidoMaterno(bloques.get(corte + 1));
    }

    private static boolean esOutsourcing(String equipo) {
        return equipo != null && normalizar(equipo).contains(AREA_OUTSOURCING);
    }

    /**
     * Mayúsculas, sin tildes, sin signos de apertura y sin espacios de sobra.
     * Los "¿"/"¡" se van porque las etiquetas de la plantilla los llevan
     * ("[1] ¿Declarado en SUNAT?") y no aportan nada al cotejo.
     */
    private static String normalizar(String texto) {
        if (texto == null)
            return "";

        String descompuesto = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);
        StringBuilder limpio = new StringBuilder(descompuesto.length());
        for (char caracter : descompuesto.toCharArray()) {
            if (Character.getType(caracter) == Character.NON_SPACING_MARK)
                continue;
            if (caracter == '¿' || caracter == '¡')
                continue;
            limpio.append(caracter);
        }
        return limpio.toString().toUpperCase().replaceAll("\\s+", " ");
    }
}
