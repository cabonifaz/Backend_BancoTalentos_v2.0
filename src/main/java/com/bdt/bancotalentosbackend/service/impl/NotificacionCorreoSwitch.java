package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.ParamsDTO;
import com.bdt.bancotalentosbackend.service.IParamsService;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.EventoNotificacion;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Decide si un evento puede enviar correo según su fila del maestro 56 (el mismo
 * interruptor que usa AutFMI). Se consulta en cada envío (sin caché), así un
 * cambio de NUM1 rige sin reiniciar. Ante cualquier configuración inválida o
 * error, ese evento no envía.
 */
@Service
@RequiredArgsConstructor
public class NotificacionCorreoSwitch {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionCorreoSwitch.class);

    static final int NUM1_HABILITADO = 1;
    static final int NUM1_DESHABILITADO = 0;

    private final IParamsService paramsService;

    public boolean permitido(EventoNotificacion evento) {
        String maestro = Constante.MAESTRO_NOTIFICACION_CORREO;
        String clave = evento.clave();
        try {
            var response = paramsService.getParamsList(maestro);
            List<ParamsDTO> filas = response != null && response.getParamsList() != null
                    ? response.getParamsList()
                    : List.of();

            // SP_PARAMETROS_LST solo devuelve filas vigentes.
            List<ParamsDTO> delEvento = filas.stream()
                    .filter(p -> p.getString1() != null && clave.equalsIgnoreCase(p.getString1().trim()))
                    .toList();

            if (delEvento.isEmpty()) {
                logger.warn("Correo {} BLOQUEADO: el maestro {} no tiene su fila vigente.", clave, maestro);
                return false;
            }
            if (delEvento.size() > 1) {
                logger.warn("Correo {} BLOQUEADO: el maestro {} tiene {} filas vigentes y debe haber una sola.",
                        clave, maestro, delEvento.size());
                return false;
            }

            Integer num1 = delEvento.get(0).getNum1();
            if (num1 == null) {
                logger.warn("Correo {} BLOQUEADO: NUM1 nulo en el maestro {}.", clave, maestro);
                return false;
            }
            if (num1 == NUM1_HABILITADO) {
                return true;
            }
            if (num1 == NUM1_DESHABILITADO) {
                logger.info("Correo {} desactivado en el maestro {} (NUM1 = 0).", clave, maestro);
                return false;
            }
            logger.warn("Correo {} BLOQUEADO: NUM1 = {} en el maestro {} (se esperaba {} o {}).",
                    clave, num1, maestro, NUM1_DESHABILITADO, NUM1_HABILITADO);
            return false;
        } catch (Exception e) {
            logger.warn("Correo {} BLOQUEADO: no se pudo consultar el maestro {}: {}", clave, maestro, e.getMessage());
            return false;
        }
    }
}
