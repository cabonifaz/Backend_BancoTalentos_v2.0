package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.dto.BlacklistClientDTO;
import com.bdt.bancotalentosbackend.model.dto.BlacklistHistoryDTO;
import com.bdt.bancotalentosbackend.model.dto.BlacklistItemDTO;
import com.bdt.bancotalentosbackend.model.dto.BlacklistValidationDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistCreateRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistRemoveRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistUpdateRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistHistoryResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistListResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistStatusResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistValidateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bdt.bancotalentosbackend.util.Common.getBaseResponse;
import static com.bdt.bancotalentosbackend.util.Common.simpleSPCall;

@Repository
@RequiredArgsConstructor
public class BlacklistRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Alta de restricción. SP_BT_LISTA_NEGRA_INS.
     */
    public BaseResponse createBlacklist(BaseRequest baseRequest, BlacklistCreateRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_BT_LISTA_NEGRA_INS");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_TALENTO", request.getIdTalento())
                    .addValue("ID_CLIENTE", request.getIdCliente())
                    .addValue("MOTIVO", request.getMotivo())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("USERNAME", baseRequest.getUsername());

            return simpleSPCall(jdbcCall, new BaseResponse(), params);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY createBlacklist: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /**
     * Actualización del motivo. SP_BT_LISTA_NEGRA_UPD.
     */
    public BaseResponse updateBlacklist(BaseRequest baseRequest, BlacklistUpdateRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_BT_LISTA_NEGRA_UPD");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_LISTA_NEGRA", request.getIdListaNegra())
                    .addValue("MOTIVO", request.getMotivo())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("USERNAME", baseRequest.getUsername());

            return simpleSPCall(jdbcCall, new BaseResponse(), params);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY updateBlacklist: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /**
     * Eliminación (soft-delete) de una restricción. SP_BT_LISTA_NEGRA_DEL.
     *
     * Da de baja una única fila. El motivo del request es el de la baja (por
     * qué se levanta la restricción), no el que tenía la restricción: es el que
     * el SP registra en el historial.
     *
     * Levantar una restricción global conservándola para algunos clientes se
     * orquesta desde el frontend encadenando esta llamada con
     * {@link #createBlacklist}.
     */
    public BaseResponse removeBlacklist(BaseRequest baseRequest, BlacklistRemoveRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_BT_LISTA_NEGRA_DEL");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_LISTA_NEGRA", request.getIdListaNegra())
                    .addValue("MOTIVO", request.getMotivo())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("USERNAME", baseRequest.getUsername());

            return simpleSPCall(jdbcCall, new BaseResponse(), params);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY removeBlacklist: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /**
     * Valida si un talento está restringido para el cliente del requerimiento.
     * SP_BT_LISTA_NEGRA_VALIDATE. result-set-1 = mensaje, result-set-2 = una
     * única fila con BLOQUEADO (1/0); la restricción global prima sobre la del
     * cliente. Si BLOQUEADO = 0 el resto de campos viene nulo.
     */
    public BlacklistValidateResponse validateBlacklist(BaseRequest baseRequest, Integer idTalento,
                                                       Integer idRequerimiento) {
        BlacklistValidateResponse response = new BlacklistValidateResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_BT_LISTA_NEGRA_VALIDATE");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_TALENTO", idTalento)
                    .addValue("ID_REQUERIMIENTO", idRequerimiento)
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("USUARIO", baseRequest.getUsername())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                response.setBaseResponse(new BaseResponse(3, "No hubo respuesta de la base de datos"));
                return response;
            }

            response.setBaseResponse(getBaseResponse(messageSet));

            if (response.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("#result-set-2");
                if (rows != null && !rows.isEmpty()) {
                    Map<String, Object> row = rows.get(0);
                    response.setValidacion(new BlacklistValidationDTO(
                            (Integer) row.get("ID_LISTA_NEGRA"),
                            (Integer) row.get("ID_TALENTO"),
                            (Integer) row.get("ID_CLIENTE"),
                            (String) row.get("CLIENTE"),
                            (String) row.get("MOTIVO"),
                            (Boolean) row.get("BLOQUEADO")));
                }
            }
            return response;
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY validateBlacklist: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    /**
     * Estado de un talento en la lista negra. SP_BT_LISTA_NEGRA_TALENTO_STATUS.
     * result-set-1 = mensaje, result-set-2 = un cliente por restricción activa
     * (0 filas = no bloqueado; global viene como "TODOS LOS CLIENTES"). Ante
     * permiso denegado o error el estado queda en no bloqueado.
     */
    public BlacklistStatusResponse getTalentBlacklistStatus(BaseRequest baseRequest, Integer idTalento) {
        BlacklistStatusResponse response = new BlacklistStatusResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_BT_LISTA_NEGRA_TALENTO_STATUS");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_TALENTO", idTalento)
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                response.setBaseResponse(new BaseResponse(3, "No hubo respuesta de la base de datos"));
                return response;
            }

            response.setBaseResponse(getBaseResponse(messageSet));

            if (response.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("#result-set-2");
                List<BlacklistClientDTO> clientes = new ArrayList<>();
                if (rows != null) {
                    for (Map<String, Object> row : rows) {
                        clientes.add(new BlacklistClientDTO(
                                (Integer) row.get("ID_CLIENTE"),
                                (String) row.get("CLIENTE")));
                    }
                }
                response.setClientes(clientes);
                response.setBloqueado(!clientes.isEmpty());
            }
            return response;
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY getTalentBlacklistStatus: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    /**
     * Listado de restricciones activas. SP_BT_LISTA_NEGRA_LST.
     * result-set-1 = mensaje + TOTAL_LISTA, result-set-2 = filas.
     *
     * La paginación es por talento (no por restricción): TOTAL_LISTA es el
     * número de talentos y el SP devuelve todas las restricciones de los
     * talentos de la página. {@code pagina} nulo trae el listado completo.
     */
    public BlacklistListResponse listBlacklist(BaseRequest baseRequest, String nombreTalento, Integer idCliente,
                                               Integer pagina) {
        BlacklistListResponse response = new BlacklistListResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_BT_LISTA_NEGRA_LST");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("NOMBRE_TALENTO", nombreTalento)
                    .addValue("ID_CLIENTE", idCliente)
                    .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
                    .addValue("N_PAG", pagina)
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("USUARIO", baseRequest.getUsername())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                response.setBaseResponse(new BaseResponse(3, "No hubo respuesta de la base de datos"));
                return response;
            }

            response.setBaseResponse(getBaseResponse(messageSet));
            response.setTotal((Integer) messageSet.get(0).get("TOTAL_LISTA"));

            if (response.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("#result-set-2");
                List<BlacklistItemDTO> registros = new ArrayList<>();
                if (rows != null) {
                    for (Map<String, Object> row : rows) {
                        registros.add(new BlacklistItemDTO(
                                (Integer) row.get("ID_LISTA_NEGRA"),
                                (Integer) row.get("ID_TALENTO"),
                                (String) row.get("NOMBRE_TALENTO"),
                                (Integer) row.get("ID_CLIENTE"),
                                (String) row.get("CLIENTE"),
                                (String) row.get("MOTIVO"),
                                (String) row.get("USUCRE"),
                                toStr(row.get("FCHCRE"))));
                    }
                }
                response.setRegistros(registros);
            }
            return response;
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY listBlacklist: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    /**
     * Historial de movimientos de un talento. SP_BT_HISTORIAL_LISTA_NEGRA_LST.
     * result-set-1 = mensaje + TOTAL_LISTA, result-set-2 = movimientos.
     *
     * Aquí la paginación sí es por fila: cada movimiento es una entrada del
     * timeline. {@code pagina} nulo trae el historial completo.
     */
    public BlacklistHistoryResponse listBlacklistHistory(BaseRequest baseRequest, Integer idTalento, Integer idCliente,
                                                         Integer pagina) {
        BlacklistHistoryResponse response = new BlacklistHistoryResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_BT_HISTORIAL_LISTA_NEGRA_LST");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_TALENTO", idTalento)
                    .addValue("ID_CLIENTE", idCliente)
                    .addValue("N_PAG", pagina)
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("USUARIO", baseRequest.getUsername())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                response.setBaseResponse(new BaseResponse(3, "No hubo respuesta de la base de datos"));
                return response;
            }

            response.setBaseResponse(getBaseResponse(messageSet));
            response.setTotal((Integer) messageSet.get(0).get("TOTAL_LISTA"));

            if (response.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("#result-set-2");
                List<BlacklistHistoryDTO> historial = new ArrayList<>();
                if (rows != null) {
                    for (Map<String, Object> row : rows) {
                        historial.add(new BlacklistHistoryDTO(
                                (Integer) row.get("ID_HISTORIAL_LISTA_NEGRA"),
                                (Integer) row.get("ID_LISTA_NEGRA"),
                                (Integer) row.get("ID_TALENTO"),
                                (Integer) row.get("ID_CLIENTE"),
                                (String) row.get("CLIENTE"),
                                (String) row.get("MOTIVO"),
                                (String) row.get("MOVIMIENTO"),
                                (String) row.get("USUCRE"),
                                toStr(row.get("FCHCRE"))));
                    }
                }
                response.setHistorial(historial);
            }
            return response;
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY listBlacklistHistory: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    private static String toStr(Object value) {
        return value != null ? value.toString() : null;
    }
}
