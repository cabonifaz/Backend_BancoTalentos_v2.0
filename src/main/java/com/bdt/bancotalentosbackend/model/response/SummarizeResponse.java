package com.bdt.bancotalentosbackend.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummarizeResponse {
  private String summary;
}
