package com.bdt.bancotalentosbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bdt.bancotalentosbackend.model.request.FileRequestV2;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.service.IFileService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/files")
public class FilesController {

  private final IFileService filesService;

  public FilesController(IFileService filesService) {
    this.filesService = filesService;
  }

  @PostMapping("document-by-url")
  public ResponseEntity<BaseResponse> getDocumentByUrl(
      @RequestBody FileRequestV2 request,
      HttpServletRequest httpServletRequest) {

    try {
      var response = this.filesService.getFileByUrl(request.getFileUrl());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      var response = new BaseResponse(3, "Ocurrión un error al obtener el archivo");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
