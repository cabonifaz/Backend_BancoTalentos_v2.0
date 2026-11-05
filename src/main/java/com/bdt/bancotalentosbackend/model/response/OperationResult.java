package com.bdt.bancotalentosbackend.model.response;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationResult<T> {
  @NonNull
  private BaseResponseFMI baseResponse;

  @Nullable
  private T data;
}
