package com.bdt.bancotalentosbackend.mcp;

import com.bdt.bancotalentosbackend.config.McpConfig;
import com.bdt.bancotalentosbackend.model.dto.ParamsDTO;
import com.bdt.bancotalentosbackend.model.request.SearchRequest;
import com.bdt.bancotalentosbackend.model.response.BlacklistHistoryResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistListResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistStatusResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistValidateResponse;
import com.bdt.bancotalentosbackend.model.response.ParamsListResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.service.IBlacklistService;
import com.bdt.bancotalentosbackend.service.IParamsService;
import com.bdt.bancotalentosbackend.service.ITalentsService;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Herramientas MCP de solo lectura (Talentos y Lista negra).
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

    // Maestros de PARAMETROS que usan los filtros de la búsqueda de talentos
    private static final String MAESTRO_HABILIDADES = "19";
    private static final String MAESTRO_NIVEL_INGLES = "16";
    private static final String MAESTRO_GRADO_ACADEMICO = "38";

    private final ITalentsService talentsService;
    private final IBlacklistService blacklistService;
    private final IParamsService paramsService;

    public record CatalogItem(Integer id, String nombre) {
    }

    @McpTool(name = "catalogos_busqueda_talentos",
            description = "Devuelve los IDs que piden los filtros de buscar_talentos. "
                    + "tipo: HABILIDADES (habilidades técnicas), NIVEL_INGLES o GRADO_ACADEMICO. "
                    + "Usar antes de filtrar por habilidades, nivel de inglés o grado.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public List<CatalogItem> catalogosBusquedaTalentos(
            McpSyncServerExchange exchange,
            @McpToolParam(description = "HABILIDADES, NIVEL_INGLES o GRADO_ACADEMICO") String tipo,
            @McpToolParam(description = "Texto para filtrar por nombre (opcional)", required = false) String buscar) {
        requireToken(exchange);

        String maestro = switch (tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT)) {
            case "HABILIDADES" -> MAESTRO_HABILIDADES;
            case "NIVEL_INGLES" -> MAESTRO_NIVEL_INGLES;
            case "GRADO_ACADEMICO" -> MAESTRO_GRADO_ACADEMICO;
            default -> throw new IllegalArgumentException(
                    "tipo debe ser HABILIDADES, NIVEL_INGLES o GRADO_ACADEMICO");
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
            @McpToolParam(description = "IDs de habilidades técnicas separados por coma (ver catalogos_busqueda_talentos)",
                    required = false) String idsHabilidades,
            @McpToolParam(description = "ID del nivel de inglés (ver catalogos_busqueda_talentos)",
                    required = false) Integer idNivelIngles,
            @McpToolParam(description = "Puesto de la experiencia laboral", required = false) String puesto,
            @McpToolParam(description = "Años mínimos de experiencia", required = false) Integer aniosExperiencia,
            @McpToolParam(description = "Nombre de la carrera o institución educativa", required = false) String educacion,
            @McpToolParam(description = "ID del grado académico (ver catalogos_busqueda_talentos)",
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

    private String requireToken(McpSyncServerExchange exchange) {
        Object token = exchange.transportContext().get(McpConfig.MCP_TOKEN_KEY);
        if (token == null || token.toString().isBlank()) {
            throw new IllegalStateException("Credencial inválida o ausente: envía X-API-Key o Authorization: Bearer <token>");
        }
        return token.toString();
    }
}
