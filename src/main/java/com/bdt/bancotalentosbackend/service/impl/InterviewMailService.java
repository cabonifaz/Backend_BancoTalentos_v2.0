package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.ParamsDTO;
import com.bdt.bancotalentosbackend.model.dto.UserContactInfoDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.response.InterviewDetailResponseDTO;
import com.bdt.bancotalentosbackend.service.IParamsService;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.EventoNotificacion;
import com.bdt.bancotalentosbackend.util.MailUtils;
import com.bdt.bancotalentosbackend.util.SafeValues;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Correo de la entrevista al candidato (con el ICS adjunto). Migrado de
 * MailService de AutFMI sin cambios de comportamiento: misma plantilla por
 * etapa, mismos CC (entrevistadores con notificación + quien ejecuta la
 * acción) y la telefónica no notifica.
 */
@Service
@RequiredArgsConstructor
public class InterviewMailService {

  private static final Logger logger = LoggerFactory.getLogger(InterviewMailService.class);

  private final MailUtils mailUtils;
  private final IParamsService paramsService;

  @Async("notificationExecutor")
  public void sendInterviewUnifiedNotification(
      InterviewDetailResponseDTO detail,
      String talentEmail,
      String talentFullName,
      BaseRequest actionUser,
      UserContactInfoDTO actionUserInfo,
      String actionType,
      byte[] icsAttachment,
      String icsFileName) {

    if (detail == null || talentEmail == null || talentEmail.trim().isEmpty()) {
      logger.warn("Missing interview detail or talent email, skipping unified notification");
      return;
    }

    // CC: all registered interviewers with a valid email + the acting user
    List<String> ccList = new ArrayList<>();
    if (detail.getEntrevistadores() != null) {
      detail.getEntrevistadores().stream()
          .filter(e -> e.email() != null && !e.email().trim().isEmpty() && e.notificacion())
          .forEach(e -> ccList.add(e.email().trim()));
    }
    if (actionUserInfo != null && actionUserInfo.email() != null && !actionUserInfo.email().trim().isEmpty()) {
      ccList.add(actionUserInfo.email().trim());
    }

    // MailUtils depura los CC (formato, duplicados y el propio TO)
    String etapa = SafeValues.safeString(detail.getEtapa()).trim();

    // Strip "CODE - " prefix from perfil (e.g. "SOL_B412 - Desarrollador Backend" → "Desarrollador Backend")
    String perfilRaw = SafeValues.safeString(detail.getPerfil());
    String position = perfilRaw.contains(" - ")
        ? perfilRaw.substring(perfilRaw.indexOf(" - ") + 3)
        : perfilRaw;

    // All registered interviewer names, comma separated
    String allInterviewers;
    List<String> interviewerNames = new ArrayList<>();
    if (detail.getEntrevistadores() != null && !detail.getEntrevistadores().isEmpty()) {
      detail.getEntrevistadores().stream()
          .filter(e -> e.fullname() != null && !e.fullname().trim().isEmpty())
          .forEach(e -> interviewerNames.add(e.fullname().trim()));
      allInterviewers = String.join(", ", interviewerNames);
    } else {
      allInterviewers = "Equipo de seleccion";
    }

    String actionUserName = actionUserInfo != null && actionUserInfo.fullName() != null
        && !actionUserInfo.fullName().trim().isEmpty()
            ? actionUserInfo.fullName().trim()
            : (actionUser != null ? SafeValues.safeString(actionUser.getUsername()) : "Sistema");
    String actionUserPhone = actionUserInfo != null ? SafeValues.safeString(actionUserInfo.telefono()) : "";

    Map<String, Object> variables = new HashMap<>();
    variables.put("candidateName", SafeValues.safeString(talentFullName));
    variables.put("position", position);
    variables.put("formattedDate", formatSpanishDate(detail.getFecha()));
    variables.put("formattedTime", formatTime12h(detail.getHora()));
    variables.put("allInterviewers", allInterviewers);
    variables.put("clientName", SafeValues.safeString(detail.getClienteResumen()));
    variables.put("enlace", SafeValues.safeString(detail.getEnlaceEntrevista()));

    // Tipo de entrevista: la fuente de verdad es ID_TIPO_ENTREVISTA (maestro 47),
    // NO la presencia/ausencia de enlace, ubicación o dirección.
    // PRESENCIAL → dirección + ubicación (Google Maps); en cualquier otro caso
    // (VIRTUAL o no resoluble/legacy) se mantiene el enlace de la videollamada.
    String tipoEntrevista = resolveTipoEntrevista(detail.getIdTipoEntrevista());
    boolean esPresencial = Constante.TIPO_ENTREVISTA_PRESENCIAL.equalsIgnoreCase(tipoEntrevista);
    boolean esTelefonica = Constante.TIPO_ENTREVISTA_TELEFONICA
        .equalsIgnoreCase(sinTildes(tipoEntrevista));

    // La entrevista telefónica no notifica al candidato: se coordina por
    // teléfono. El guard vive aquí, y no sólo en el frontend, para que ninguna
    // otra vía (reintento, integración futura) termine mandando el correo.
    if (esTelefonica) {
      logger.info("Interview {} is TELEFONICA: notification skipped.", detail.getId());
      return;
    }

    variables.put("esPresencial", esPresencial);
    variables.put("esTelefonica", esTelefonica);
    variables.put("ubicacion", SafeValues.safeString(detail.getUbicacion()));
    variables.put("direccion", SafeValues.safeString(detail.getDireccion()));

    variables.put("accionUsuarioNombre", actionUserName);
    variables.put("accionUsuarioTelefono", actionUserPhone);

    String templateName;
    String subject;
    if ("Entrevista tecnica con líder técnico fractal".equalsIgnoreCase(etapa)) {
      templateName = "interview-tecnica-fractal";
      subject = "Entrevista Técnica - FRACTAL SOLUCIONES TI";
    } else if ("Entrevista técnica con cliente".equalsIgnoreCase(etapa)) {
      templateName = "interview-tecnica-cliente";
      subject = "Entrevista Técnica - FRACTAL SOLUCIONES TI";
    } else {
      templateName = "interview-general";
      subject = "¡Queremos conocerte! - FRACTAL SOLUCIONES TI";
    }

    // Inline images: header logo + signature assets
    Map<String, Resource> inlineResources = new LinkedHashMap<>();
    addInlineResourceIfPresent(inlineResources, "sig-logo", "assets/fractal-transparente.png");
    addInlineResourceIfPresent(inlineResources, "sig-mosaic", "assets/cubo-fractal.png");
    addInlineResourceIfPresent(inlineResources, "sig-linkedin", "assets/linkedin.png");
    addInlineResourceIfPresent(inlineResources, "sig-youtube", "assets/youtube.png");

    logger.info("Sending interview notification ({}, {}) to: {} | CC: {} recipients | ICS: {}",
        templateName, actionType, talentEmail.trim(), ccList.size(),
        (icsAttachment != null && icsAttachment.length > 0));
    boolean enviado = mailUtils.sendEmailWithHtmlTemplate(EventoNotificacion.ENTREVISTA, talentEmail.trim(),
        ccList, subject, templateName,
        variables, inlineResources, icsFileName, icsAttachment);
    if (enviado) {
      logger.info("Interview notification dispatched.");
    } else {
      logger.info("Interview notification not sent.");
    }
  }

  /** El maestro 47 escribe "TELEFÓNICA" con tilde; la comparación no debe depender de eso. */
  private String sinTildes(String texto) {
    if (texto == null) {
      return "";
    }

    // Sin expresion regular a proposito: descomponer y descartar los acentos
    // (marcas sin espaciado) evita depender de un escape dentro del literal.
    String descompuesto = Normalizer.normalize(texto, Normalizer.Form.NFD);
    StringBuilder limpio = new StringBuilder(descompuesto.length());

    for (int i = 0; i < descompuesto.length(); i++) {
      char caracter = descompuesto.charAt(i);
      if (Character.getType(caracter) != Character.NON_SPACING_MARK) {
        limpio.append(caracter);
      }
    }

    return limpio.toString().trim();
  }

  private String resolveTipoEntrevista(Integer idTipoEntrevista) {
    if (idTipoEntrevista == null) {
      return "";
    }
    try {
      var response = paramsService.getParamsList(Constante.TIPO_ENTREVISTA);
      if (response == null || response.getParamsList() == null) {
        return "";
      }
      return response.getParamsList().stream()
          .filter(p -> idTipoEntrevista.equals(p.getNum1()))
          .map(ParamsDTO::getString1)
          .filter(s -> s != null)
          .findFirst()
          .orElse("");
    } catch (Exception e) {
      logger.warn("No se pudo resolver el tipo de entrevista (id={}): {}", idTipoEntrevista, e.getMessage());
      return "";
    }
  }

  private void addInlineResourceIfPresent(Map<String, Resource> inlineResources, String cid, String classpath) {
    try {
      ClassPathResource resource = new ClassPathResource(classpath);
      if (resource.exists()) {
        inlineResources.put(cid, resource);
      }
    } catch (Exception e) {
      logger.warn("Resource '{}' not available, sending without it: {}", classpath, e.getMessage());
    }
  }

  private String formatSpanishDate(String fechaStr) {
    if (fechaStr == null || fechaStr.trim().isEmpty()) return "";
    try {
      LocalDate date;
      String trimmed = fechaStr.trim();
      if (trimmed.length() == 10 && trimmed.charAt(4) == '-') {
        date = LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      } else if (trimmed.contains("/")) {
        date = LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
      } else {
        return fechaStr;
      }
      String formattedDate = date.format(
          DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-PE")));
      return Character.toUpperCase(formattedDate.charAt(0)) + formattedDate.substring(1);
    } catch (Exception e) {
      logger.warn("Could not format date '{}': {}", fechaStr, e.getMessage());
      return fechaStr;
    }
  }

  private String formatTime12h(String horaStr) {
    if (horaStr == null || horaStr.trim().isEmpty()) return "";
    try {
      LocalTime time;
      String trimmed = horaStr.trim();
      if (trimmed.length() == 8) {
        time = LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("HH:mm:ss"));
      } else if (trimmed.length() == 5) {
        time = LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("HH:mm"));
      } else {
        return horaStr;
      }
      return time.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US));
    } catch (Exception e) {
      logger.warn("Could not format time '{}': {}", horaStr, e.getMessage());
      return horaStr;
    }
  }
}
