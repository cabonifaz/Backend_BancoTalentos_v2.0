package com.bdt.bancotalentosbackend.config;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.transport.WebMvcStreamableServerTransportProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * Transporte del servidor MCP (Streamable HTTP en /bdt/mcp).
 *
 * <p>
 * Reemplaza al que crea Spring AI solo para añadir el {@code contextExtractor},
 * que resuelve el JWT con el que las herramientas (BdtMcpTools) llaman a los
 * services:
 * <ul>
 * <li>{@code X-API-Key} (integraciones como el RAG de Fractal): si coincide con
 * {@code mcp.api-key}, se genera un JWT del usuario de servicio. Ese JWT nunca
 * sale del backend; los permisos los da el rol del usuario de servicio.</li>
 * <li>{@code Authorization: Bearer <JWT>} (pruebas con un usuario real, p. ej.
 * desde el MCP Inspector): se usa tal cual.</li>
 * </ul>
 * Sin credencial válida el contexto va vacío y la herramienta responde con error.
 * Con {@code mcp.api-key} vacío, la API key queda deshabilitada.
 */
@Configuration
public class McpConfig {

    public static final String MCP_TOKEN_KEY = "token";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String API_KEY_HEADER = "X-API-Key";

    @Value("${mcp.api-key:}")
    private String apiKey;

    @Value("${mcp.service-user.id-usuario:}")
    private Integer serviceUserId;

    @Value("${mcp.service-user.id-empresa:}")
    private Integer serviceEmpresaId;

    @Value("${mcp.service-user.id-rol:}")
    private Integer serviceRolId;

    @Value("${mcp.service-user.username:}")
    private String serviceUsername;

    @Bean
    public WebMvcStreamableServerTransportProvider webMvcStreamableServerTransportProvider(ObjectMapper objectMapper,
            JWTHelper jwtHelper) {
        return WebMvcStreamableServerTransportProvider.builder()
                .jsonMapper(new JacksonMcpJsonMapper(objectMapper))
                .mcpEndpoint("/mcp")
                .contextExtractor(request -> {
                    String requestApiKey = request.headers().firstHeader(API_KEY_HEADER);
                    if (requestApiKey != null) {
                        return isValidApiKey(requestApiKey)
                                ? McpTransportContext.create(Map.of(MCP_TOKEN_KEY, serviceUserToken(jwtHelper)))
                                : McpTransportContext.EMPTY;
                    }

                    String header = request.headers().firstHeader("Authorization");
                    if (header == null || !header.startsWith(BEARER_PREFIX)) {
                        return McpTransportContext.EMPTY;
                    }
                    return McpTransportContext.create(
                            Map.of(MCP_TOKEN_KEY, header.substring(BEARER_PREFIX.length()).trim()));
                })
                .build();
    }

    // Comparación en tiempo constante para no filtrar la clave por tiempos de respuesta
    private boolean isValidApiKey(String requestApiKey) {
        if (apiKey == null || apiKey.isBlank() || serviceUserId == null || serviceRolId == null) {
            return false;
        }
        return MessageDigest.isEqual(
                apiKey.getBytes(StandardCharsets.UTF_8),
                requestApiKey.trim().getBytes(StandardCharsets.UTF_8));
    }

    // Solo lleva los claims que lee JWTHelper.decodeToken; se firma por petición (HMAC, barato)
    private String serviceUserToken(JWTHelper jwtHelper) {
        UserDTO user = new UserDTO();
        user.setIdUsuario(serviceUserId);
        user.setIdEmpresa(serviceEmpresaId);
        user.setUsuario(serviceUsername);
        user.setNombres("MCP");
        user.setApellidos(serviceUsername);
        user.setIdRoles(List.of(serviceRolId));
        user.setRoles(List.of());
        user.setRoutes(List.of());
        return jwtHelper.generateToken(user);
    }
}
