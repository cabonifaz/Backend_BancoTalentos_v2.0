package com.bdt.bancotalentosbackend.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data()
@EqualsAndHashCode(callSuper = false)
public class FileResponseV2 extends BaseResponse {

  private String fileBase64;
  private String filename;

  public FileResponseV2(Integer messageId, String message) {
    super(messageId, message);
  }

}
