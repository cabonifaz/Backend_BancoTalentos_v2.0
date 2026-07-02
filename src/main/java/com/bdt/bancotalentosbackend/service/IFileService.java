package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.response.BaseResponse;

public interface IFileService {

  BaseResponse getFileByUrl(String fileUrl);

}
