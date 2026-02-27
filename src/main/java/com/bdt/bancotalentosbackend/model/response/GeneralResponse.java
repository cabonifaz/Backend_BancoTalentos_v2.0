package com.bdt.bancotalentosbackend.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GeneralResponse<T> extends BaseResponse {
  private T data;

  public GeneralResponse(Integer idMensaje, String mensaje, T data) {
    super(idMensaje, mensaje);
    this.data = data;
  }

  public static <T> GeneralResponse<T> of(Integer idMensaje, String mensaje, T data) {
    return new GeneralResponse<>(idMensaje, mensaje, data);
  }

  public static <T> GeneralResponse<T> ok(T data) {
    return new GeneralResponse<>(2, "OK", data);
  }

  public static <T> GeneralResponse<T> error(String mensaje) {
    return new GeneralResponse<>(3, mensaje, null);
  }
}