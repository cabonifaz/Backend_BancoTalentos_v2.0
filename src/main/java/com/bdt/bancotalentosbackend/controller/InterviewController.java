package com.bdt.bancotalentosbackend.controller;

import java.util.List;

import com.bdt.bancotalentosbackend.model.response.InterviewResponseDTO;
import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.InterviewUploadConfirmRequest;
import com.bdt.bancotalentosbackend.model.request.InterviewUploadUrlRequest;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.dto.InterviewByTalentDTO;
import com.bdt.bancotalentosbackend.model.dto.InterviewQuestionDTO;
import com.bdt.bancotalentosbackend.model.request.InterviewListRequest;
import com.bdt.bancotalentosbackend.model.request.InterviewQuestionUpdateRequest;
import com.bdt.bancotalentosbackend.model.request.InterviewQuestionsRequest;
import com.bdt.bancotalentosbackend.model.request.InterviewRequest;
import com.bdt.bancotalentosbackend.model.request.InterviewUpdateRequest;
import com.bdt.bancotalentosbackend.model.request.InterviewDownloadFileRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponseFMI;
import com.bdt.bancotalentosbackend.model.response.OperationResult;
import com.bdt.bancotalentosbackend.model.response.PaginatedResponse;
import com.bdt.bancotalentosbackend.service.impl.InterviewService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/interviews")
@Tag(name = "Interviews", description = "API para la gestión de entrevistas")
@RequiredArgsConstructor
public class InterviewController {

  private final JWTHelper jwt;
  private final InterviewService interviewService;

  @PostMapping("/create")
  public ResponseEntity<?> createInterview(
      @RequestBody InterviewRequest interviewRequest,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.CREATE_INTERVIEW);
      // Se devuelve el OperationResult completo (incluye el id creado) para que el
      // frontend pueda generar y subir el ICS de la entrevista.
      OperationResult<Integer> result = this.interviewService.createInterview(interviewRequest, baseRequest);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.ok(new OperationResult<>(new BaseResponseFMI(3, "Error al obtener el token"), null));
    }
  }

  @PostMapping("/list")
  public ResponseEntity<OperationResult<PaginatedResponse<InterviewResponseDTO>>> listInterviews(
      @RequestBody InterviewListRequest interviewListRequest,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LIST_INTERVIEW);
      var result = this.interviewService.listInterviews(interviewListRequest,
          baseRequest);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new OperationResult<>(new BaseResponseFMI(3, "Error al listar las entrevistas"), null));
    }
  }

  @GetMapping("/detail/{id}")
  public ResponseEntity<?> getInterviewById(
      @PathVariable("id") Integer id,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user,
          Constante.LIST_INTERVIEW);

      var result = this.interviewService.getInterviewById(id, baseRequest);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new OperationResult<>(new BaseResponseFMI(3, "Error al obtener el detalle de la entrevista"), null));
    }
  }

  @PostMapping("/update")
  public ResponseEntity<BaseResponseFMI> updateInterview(
      @RequestBody InterviewUpdateRequest updateRequest,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.UPDATE_INTERVIEW);

      OperationResult<Void> result = this.interviewService.updateInterview(updateRequest, baseRequest);
      return ResponseEntity.ok(result.getBaseResponse());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          new BaseResponseFMI(3, "Error de autenticación o token inválido"));
    }
  }

  /**
   * Entrevistas de un talento, opcionalmente de un solo tipo. La usa el atajo
   * "Entrevista telefónica" del detalle del talento para saber si ya tuvo una y
   * cuál fue la última.
   */
  @GetMapping("/by-talent")
  public ResponseEntity<?> listInterviewsByTalent(
      @RequestParam("idTalento") Integer idTalento,
      @RequestParam(value = "idTipoEntrevista", required = false) Integer idTipoEntrevista,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LIST_INTERVIEW);

      OperationResult<List<InterviewByTalentDTO>> result =
          this.interviewService.listInterviewsByTalent(idTalento, idTipoEntrevista, baseRequest);

      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.ok(
          new OperationResult<>(new BaseResponseFMI(3, "Error al obtener el token"), null));
    }
  }

  // ─── Preguntas y respuestas (entrevista telefónica) ──────────────────────

  /**
   * Alta en bloque de las respuestas de una entrevista. Se llama después de
   * crearla —cuando ya hay id— igual que la subida del ICS.
   */
  @PostMapping("/questions")
  public ResponseEntity<BaseResponseFMI> saveInterviewQuestions(
      @RequestBody InterviewQuestionsRequest request,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.UPDATE_INTERVIEW);

      OperationResult<Void> result = this.interviewService.saveInterviewQuestions(
          request.getIdEntrevista(),
          request.getPreguntas(),
          baseRequest);

      return ResponseEntity.ok(result.getBaseResponse());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          new BaseResponseFMI(3, "Error de autenticación o token inválido"));
    }
  }

  /** Edición de una respuesta (habitualmente, su texto). */
  @PostMapping("/questions/update")
  public ResponseEntity<BaseResponseFMI> updateInterviewQuestion(
      @RequestBody InterviewQuestionUpdateRequest request,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.UPDATE_INTERVIEW);

      OperationResult<Void> result = this.interviewService.updateInterviewQuestion(
          request.getIdRespuesta(),
          request.getIdPregunta(),
          request.getRespuesta(),
          baseRequest);

      return ResponseEntity.ok(result.getBaseResponse());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          new BaseResponseFMI(3, "Error de autenticación o token inválido"));
    }
  }

  /** Baja lógica de una respuesta. */
  @PostMapping("/questions/remove")
  public ResponseEntity<BaseResponseFMI> removeInterviewQuestion(
      @RequestParam("idRespuesta") Integer idRespuesta,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.UPDATE_INTERVIEW);

      OperationResult<Void> result = this.interviewService.deleteInterviewQuestion(idRespuesta, baseRequest);

      return ResponseEntity.ok(result.getBaseResponse());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          new BaseResponseFMI(3, "Error de autenticación o token inválido"));
    }
  }

  /** Respuestas vigentes de una entrevista. */
  @GetMapping("/questions/{idEntrevista}")
  public ResponseEntity<?> listInterviewQuestions(
      @PathVariable("idEntrevista") Integer idEntrevista,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.VIEW_INTERVIEW);

      OperationResult<List<InterviewQuestionDTO>> result =
          this.interviewService.listInterviewQuestions(idEntrevista, baseRequest);

      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.ok(
          new OperationResult<>(new BaseResponseFMI(3, "Error al obtener el token"), null));
    }
  }

  /*@PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponseFMI> uploadInterviewFile(
      @RequestParam("idInterview") Integer idInterview,
      @RequestParam("idFileType") Integer idFileType,
      @RequestParam("file") MultipartFile file,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);
      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.UPDATE_INTERVIEW);

      if (file.isEmpty()) {
        return ResponseEntity.badRequest().body(new BaseResponseFMI(3, "El archivo está vacío."));
      }

      OperationResult<Void> result = this.interviewService.uploadInterviewFile(
          idInterview, idFileType, file, baseRequest);

      return ResponseEntity.ok(result.getBaseResponse());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          new BaseResponseFMI(3, "Error de autenticación o token inválido"));
    }
  }*/

  @PostMapping("/file/upload-url")
  public ResponseEntity<?> generateUploadUrl(
      @RequestBody InterviewUploadUrlRequest request,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);

      BaseRequest baseRequest =
          Common.createBaseRequest(user, Constante.UPLOAD_DOWNLOAD_INTERVIEW_FILE);

      var result = interviewService.generateUploadUrl(request, baseRequest);

      return ResponseEntity.ok(result);

    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new BaseResponseFMI(3, "Error generando URL de carga"));
    }
  }

  @PostMapping("/file/confirm-upload")
  public ResponseEntity<BaseResponseFMI> confirmUpload(
      @RequestBody InterviewUploadConfirmRequest request,
      HttpServletRequest httpServletRequest) {

    try {

      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);

      BaseRequest baseRequest =
          Common.createBaseRequest(user, Constante.UPLOAD_DOWNLOAD_INTERVIEW_FILE);

      OperationResult<Void> result =
          interviewService.confirmUpload(request, baseRequest);

      return ResponseEntity.ok(result.getBaseResponse());

    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new BaseResponseFMI(3, "Error confirmando archivo"));
    }
  }

  @PostMapping("/file/remove")
  public ResponseEntity<BaseResponseFMI> removeInterviewFile(
      @RequestParam("fileId") Integer fileId,
      HttpServletRequest httpServletRequest) {

    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      UserDTO user = jwt.decodeToken(token);

      BaseRequest baseRequest = Common.createBaseRequest(user, Constante.UPLOAD_DOWNLOAD_INTERVIEW_FILE);

      OperationResult<Void> result = this.interviewService.deleteInterviewFile(
          fileId,
          baseRequest);

      return ResponseEntity.ok(result.getBaseResponse());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          new BaseResponseFMI(3, "Error de autenticación o token inválido"));
    }
  }
  
  @PostMapping("/file/download-url")
  public ResponseEntity<?> downloadUrl(
      @RequestBody InterviewDownloadFileRequest request,
      HttpServletRequest httpServletRequest
  ) {
      try {

        String token = JWTHelper.extractToken(httpServletRequest);
        UserDTO user = jwt.decodeToken(token);

        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.UPLOAD_DOWNLOAD_INTERVIEW_FILE);

        return ResponseEntity.ok(interviewService.generateDownloadUrl(request, baseRequest));

      } catch (Exception e) {

        return ResponseEntity.status(500)
            .body(new BaseResponseFMI(3, "Error generando URL de descarga"));
      }
      
  }
}
