package com.bdt.bancotalentosbackend.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummarizeRequest {
  private String activities;
  private String instructions;
}
