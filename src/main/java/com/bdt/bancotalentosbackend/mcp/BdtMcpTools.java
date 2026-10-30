package com.bdt.bancotalentosbackend.mcp;

import com.bdt.bancotalentosbackend.config.McpConfig;
import com.bdt.bancotalentosbackend.model.dto.InterviewByTalentDTO;
import com.bdt.bancotalentosbackend.model.dto.InterviewQuestionDTO;
import com.bdt.bancotalentosbackend.model.dto.ParamsDTO;
import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionInterviewsDTO;
import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionLabelCountDTO;
import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionPerformanceRowDTO;
import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionSummaryDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.InterviewListRequest;
import com.bdt.bancotalentosbackend.model.request.SearchRequest;
import com.bdt.bancotalentosbackend.model.request.SelectionFilterRequest;
import com.bdt.bancotalentosbackend.model.response.BlacklistHistoryResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistListResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistStatusResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistValidateResponse;
import com.bdt.bancotalentosbackend.model.response.InterviewDetailResponseDTO;
import com.bdt.bancotalentosbackend.model.response.InterviewResponseDTO;
import com.bdt.bancotalentosbackend.model.response.OperationResult;
import com.bdt.bancotalentosbackend.model.response.PaginatedResponse;
import com.bdt.bancotalentosbackend.model.response.ParamsListResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.service.IBlacklistService;
import com.bdt.bancotalentosbackend.service.IParamsService;
import com.bdt.bancotalentosbackend.service.ITalentsService;
import com.bdt.bancotalentosbackend.service.impl.InterviewService;
import com.bdt.bancotalentosbackend.service.impl.SelectionService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Herramientas MCP de solo lectura (Talentos, Lista negra, Entrevistas y Selección).
 *
 * <p>
 * Reutilizan los services de los controllers con el JWT que resuelve McpConfig
 * (el del header {@code Authorization: Bearer} o, con {@code X-API-Key}, uno del
 * usuario de servicio), así que los permisos los siguen validando los SP
 * (SP_VERIFY_FUNCIONALIDAD) igual que desde el frontend.
 */
@Component
@RequiredArgsConstructor
public class BdtMcpTools {

    // Maestros de PARAMETROS que usan los filtros de talentos y entrevistas
    private static final String MAESTRO_HABILIDADES = "19";
    private static final String MAESTRO_NIVEL_INGLES = "16";
    private static final String MAESTRO_GRADO_ACADEMICO = "38";
    private static final String MAESTRO_ESTADO_ENTREVISTA = "43";
    private static final String MAESTRO_ETAPA_ENTREVISTA = "44";
    private static final String TIPOS_CATALOGO =
            "HABILIDADES, NIVEL_INGLES, GRADO_ACADEMICO, ESTADO_ENTREVISTA o ETAPA_ENTREVISTA";

    private final ITalentsService talentsService;
    private final IBlacklistService blacklistService;
    private final IParamsService paramsService;
    private final InterviewService interviewService;
    private final SelectionService selectionService;
    private final JWTHelper jwtHelper;

    public record CatalogItem(Integer id, String nombre) {
    }

    @McpTool(name = "catalogos_filtros",
            description = "Devuelve los IDs que piden los filtros de buscar_talentos y listar_entrevistas. "
                    + "tipo: HABILIDADES (habilidades técnicas), NIVEL_INGLES, GRADO_ACADEMICO, "
                    + "ESTADO_ENTREVISTA o ETAPA_ENTREVISTA. Usar antes de filtrar por cualquiera de ellos.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public List<CatalogItem> catalogosFiltros(
            McpSyncServerExchange exchange,
            @McpToolParam(description = TIPOS_CATALOGO) String tipo,
            @McpToolParam(description = "Texto para filtrar por nombre (opcional)", required = false) String buscar) {
        requireToken(exchange);

        String maestro = switch (tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT)) {
            case "HABILIDADES" -> MAESTRO_HABILIDADES;
            case "NIVEL_INGLES" -> MAESTRO_NIVEL_INGLES;
            case "GRADO_ACADEMICO" -> MAESTRO_GRADO_ACADEMICO;
            case "ESTADO_ENTREVISTA" -> MAESTRO_ESTADO_ENTREVISTA;
            case "ETAPA_ENTREVISTA" -> MAESTRO_ETAPA_ENTREVISTA;
            default -> throw new IllegalArgumentException("tipo debe ser " + TIPOS_CATALOGO);
        };

        ParamsListResponse response = paramsService.getParamsList(maestro);
        List<ParamsDTO> params = response.getParamsList() == null ? List.of() : response.getParamsList();
        String filtro = buscar == null ? "" : buscar.trim().toLowerCase(Locale.ROOT);

        return params.stream()
                .filter(p -> filtro.isEmpty()
                        || (p.getString1() != null && p.getString1().toLowerCase(Locale.ROOT).contains(filtro)))
                .map(p -> new CatalogItem(p.getNum1(), p.getString1()))
                .toList();
    }

    @McpTool(name = "buscar_talentos",
            description = "Busca talentos del Banco de Talentos con paginación (página desde 1). "
                    + "Todos los filtros son opcionales y se combinan.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public TalentsListResponse buscarTalentos(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "Número de página, desde 1 (por defecto 1)", required = false) Integer pagina,
            @McpToolParam(description = "Texto libre: nombre del talento o puesto", required = false) String buscar,
            @McpToolParam(description = "IDs de habilidades técnicas separados por coma (ver catalogos_filtros)",
                    required = false) String idsHabilidades,
            @McpToolParam(description = "ID del nivel de inglés (ver catalogos_filtros)",
                    required = false) Integer idNivelIngles,
            @McpToolParam(description = "Puesto de la experiencia laboral", required = false) String puesto,
            @McpToolParam(description = "Años mínimos de experiencia", required = false) Integer aniosExperiencia,
            @McpToolParam(description = "Nombre de la carrera o institución educativa", required = false) String educacion,
            @McpToolParam(description = "ID del grado académico (ver catalogos_filtros)",
                    required = false) Integer idGradoAcademico) {
        String token = requireToken(exchange);
        SearchRequest searchRequest = new SearchRequest(pagina == null ? 1 : pagina, buscar, idsHabilidades,
                idNivelIngles, puesto, aniosExperiencia, educacion, idGradoAcademico, null);
        return talentsService.getTalents(token, searchRequest);
    }

    @McpTool(name = "ver_talento",
            description = "Detalle completo de un talento: datos personales, contacto, expectativa salarial, "
                    + "experiencia, educación, idiomas, habilidades y retroalimentación.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public TalentResponse verTalento(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "ID del talento") Integer idTalento) {
        return talentsService.getTalentById(requireToken(exchange), idTalento, true);
    }

    @McpTool(name = "listar_lista_negra",
            description = "Lista los talentos con restricciones activas en la lista negra, por cliente o globales. "
                    + "Paginado (página desde 1); sin página devuelve todo.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public BlacklistListResponse listarListaNegra(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "Nombre del talento", required = false) String nombreTalento,
            @McpToolParam(description = "ID del cliente", required = false) Integer idCliente,
            @McpToolParam(description = "Número de página, desde 1", required = false) Integer pagina) {
        return blacklistService.listBlacklist(requireToken(exchange), nombreTalento, idCliente, pagina);
    }

    @McpTool(name = "historial_lista_negra",
            description = "Historial de movimientos en la lista negra de un talento (restricciones y retiros, con motivo).",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public BlacklistHistoryResponse historialListaNegra(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "ID del talento") Integer idTalento,
            @McpToolParam(description = "ID del cliente", required = false) Integer idCliente,
            @McpToolParam(description = "Número de página, desde 1", required = false) Integer pagina) {
        return blacklistService.listBlacklistHistory(requireToken(exchange), idTalento, idCliente, pagina);
    }

    @McpTool(name = "estado_lista_negra_talento",
            description = "Indica si un talento tiene alguna restricción activa en la lista negra, sin importar el cliente.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public BlacklistStatusResponse estadoListaNegraTalento(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "ID del talento") Integer idTalento) {
        return blacklistService.getTalentBlacklistStatus(requireToken(exchange), idTalento);
    }

    @McpTool(name = "validar_lista_negra_para_rq",
            description = "Valida si un talento está restringido para el cliente de un requerimiento (o de forma global). "
                    + "Devuelve la restricción que aplica con cliente y motivo.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public BlacklistValidateResponse validarListaNegraParaRq(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "ID del talento") Integer idTalento,
            @McpToolParam(description = "ID del requerimiento") Integer idRequerimiento) {
        return blacklistService.validateBlacklist(requireToken(exchange), idTalento, idRequerimiento);
    }

    @McpTool(name = "listar_entrevistas",
            description = "Lista entrevistas con paginación (página desde 1). Filtros opcionales: texto "
                    + "(talento), cliente, estado, etapa (IDs en catalogos_filtros) y fecha (yyyy-MM-dd).",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public OperationResult<PaginatedResponse<InterviewResponseDTO>> listarEntrevistas(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "Número de página, desde 1 (por defecto 1)", required = false) Integer pagina,
            @McpToolParam(description = "Texto libre: nombre del talento", required = false) String buscar,
            @McpToolParam(description = "ID del cliente", required = false) Integer idCliente,
            @McpToolParam(description = "ID del estado de la entrevista (ver catalogos_filtros)",
                    required = false) Integer idEstado,
            @McpToolParam(description = "ID de la etapa de la entrevista (ver catalogos_filtros)",
                    required = false) Integer idEtapa,
            @McpToolParam(description = "Fecha de la entrevista, yyyy-MM-dd", required = false) String fecha) {
        BaseRequest baseRequest = baseRequest(exchange, Constante.LIST_INTERVIEW);
        InterviewListRequest request = new InterviewListRequest(pagina == null ? 1 : pagina, buscar, idCliente,
                idEstado, idEtapa, fecha);
        return interviewService.listInterviews(request, baseRequest);
    }

    @McpTool(name = "ver_entrevista",
            description = "Detalle de una entrevista: talento, requerimiento, cliente, etapa, estado, tipo, fecha, "
                    + "entrevistadores, enlace o dirección y archivos.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public OperationResult<InterviewDetailResponseDTO> verEntrevista(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "ID de la entrevista") Integer idEntrevista) {
        return interviewService.getInterviewById(idEntrevista, baseRequest(exchange, Constante.LIST_INTERVIEW));
    }

    @McpTool(name = "preguntas_entrevista",
            description = "Preguntas y respuestas registradas en una entrevista (sobre todo las telefónicas).",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public OperationResult<List<InterviewQuestionDTO>> preguntasEntrevista(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "ID de la entrevista") Integer idEntrevista) {
        return interviewService.listInterviewQuestions(idEntrevista, baseRequest(exchange, Constante.VIEW_INTERVIEW));
    }

    @McpTool(name = "entrevistas_de_talento",
            description = "Entrevistas que ha tenido un talento, opcionalmente de un solo tipo "
                    + "(presencial, virtual o telefónica, por su ID del maestro de tipos).",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public OperationResult<List<InterviewByTalentDTO>> entrevistasDeTalento(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "ID del talento") Integer idTalento,
            @McpToolParam(description = "ID del tipo de entrevista", required = false) Integer idTipoEntrevista) {
        return interviewService.listInterviewsByTalent(idTalento, idTipoEntrevista,
                baseRequest(exchange, Constante.LIST_INTERVIEW));
    }

    @McpTool(name = "seleccion_resumen",
            description = "Indicadores de Selección en un rango de fechas: total de entrevistas y total de ingresos.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public OperationResult<SelectionSummaryDTO> seleccionResumen(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "Fecha inicial, yyyy-MM-dd") String fechaIni,
            @McpToolParam(description = "Fecha final, yyyy-MM-dd") String fechaFin,
            @McpToolParam(description = "ID del cliente (vacío = todos)", required = false) Integer idCliente) {
        return selectionService.getResumen(selectionFilter(fechaIni, fechaFin, idCliente, null),
                baseRequest(exchange, Constante.SELECCION_ESTADISTICAS));
    }

    @McpTool(name = "seleccion_entrevistas",
            description = "Entrevistas de Selección en un rango de fechas: total, serie por periodo y conteo por "
                    + "usuario de selección. Con usuario, también su total y su serie.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public OperationResult<SelectionInterviewsDTO> seleccionEntrevistas(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "Fecha inicial, yyyy-MM-dd") String fechaIni,
            @McpToolParam(description = "Fecha final, yyyy-MM-dd") String fechaFin,
            @McpToolParam(description = "ID del cliente (vacío = todos)", required = false) Integer idCliente,
            @McpToolParam(description = "Usuario (login) de selección para su desglose", required = false)
            String usuario) {
        return selectionService.getEntrevistas(selectionFilter(fechaIni, fechaFin, idCliente, usuario),
                baseRequest(exchange, Constante.SELECCION_ESTADISTICAS));
    }

    @McpTool(name = "seleccion_ingresos",
            description = "Ingresos de talentos en un rango de fechas, agrupados (etiqueta y cantidad).",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public OperationResult<List<SelectionLabelCountDTO>> seleccionIngresos(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "Fecha inicial, yyyy-MM-dd") String fechaIni,
            @McpToolParam(description = "Fecha final, yyyy-MM-dd") String fechaFin,
            @McpToolParam(description = "ID del cliente (vacío = todos)", required = false) Integer idCliente) {
        return selectionService.getIngresos(selectionFilter(fechaIni, fechaFin, idCliente, null),
                baseRequest(exchange, Constante.SELECCION_ESTADISTICAS));
    }

    @McpTool(name = "seleccion_rendimiento",
            description = "Rendimiento de Selección por cliente en un rango de fechas: entrevistas e ingresos.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public OperationResult<List<SelectionPerformanceRowDTO>> seleccionRendimiento(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "Fecha inicial, yyyy-MM-dd") String fechaIni,
            @McpToolParam(description = "Fecha final, yyyy-MM-dd") String fechaFin,
            @McpToolParam(description = "ID del cliente (vacío = todos)", required = false) Integer idCliente) {
        return selectionService.getRendimiento(selectionFilter(fechaIni, fechaFin, idCliente, null),
                baseRequest(exchange, Constante.SELECCION_ESTADISTICAS));
    }

    private SelectionFilterRequest selectionFilter(String fechaIni, String fechaFin, Integer idCliente,
            String usuario) {
        SelectionFilterRequest request = new SelectionFilterRequest();
        request.setFechaIni(fechaIni);
        request.setFechaFin(fechaFin);
        request.setIdCliente(idCliente);
        request.setUsucre(usuario);
        return request;
    }

    // Entrevistas y Selección trabajan con BaseRequest (como sus controllers), no con el token
    private BaseRequest baseRequest(McpSyncServerExchange exchange, String funcionalidad) {
        return Common.createBaseRequest(jwtHelper.decodeToken(requireToken(exchange)), funcionalidad);
    }

    private String requireToken(McpSyncServerExchange exchange) {
        Object token = exchange.transportContext().get(McpConfig.MCP_TOKEN_KEY);
        if (token == null || token.toString().isBlank()) {
            throw new IllegalStateException("Credencial inválida o ausente: envía X-API-Key o Authorization: Bearer <token>");
        }
        return token.toString();
    }
}
