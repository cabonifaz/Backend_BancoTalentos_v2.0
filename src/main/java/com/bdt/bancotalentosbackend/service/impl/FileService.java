package com.bdt.bancotalentosbackend.service.impl;

import org.springframework.stereotype.Service;

import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.FileResponseV2;
import com.bdt.bancotalentosbackend.service.IFileService;
import com.bdt.bancotalentosbackend.util.FileUtils;

@Service
public class FileService implements IFileService {

  @Override
  public BaseResponse getFileByUrl(String fileUrl) {
    var fileBase64 = FileUtils.cargarArchivoAws(fileUrl);

    if (fileBase64.equals(""))
      return new FileResponseV2(3, "No se pudo descargar el archivo");

    var response = new FileResponseV2(2, "Archivo descargado");
    response.setFileBase64(fileBase64);
    return response;
  }

}
