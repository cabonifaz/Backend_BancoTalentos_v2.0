package com.bdt.bancotalentosbackend.service.impl;

import java.util.List;

import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionInterviewsDTO;
import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionLabelCountDTO;
import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionPerformanceRowDTO;
import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionSummaryDTO;
import com.bdt.bancotalentosbackend.model.dto.seleccion.SelectionUserDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.SelectionFilterRequest;
import com.bdt.bancotalentosbackend.model.response.OperationResult;
import com.bdt.bancotalentosbackend.repository.SelectionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/** Estadísticas del módulo Selección. La agregación vive en los SP. */
@Service
@RequiredArgsConstructor
public class SelectionService {

  private final SelectionRepository selectionRepository;

  public OperationResult<SelectionSummaryDTO> getResumen(SelectionFilterRequest request, BaseRequest baseRequest) {
    return this.selectionRepository.getResumen(request, baseRequest);
  }

  public OperationResult<SelectionInterviewsDTO> getEntrevistas(SelectionFilterRequest request, BaseRequest baseRequest) {
    return this.selectionRepository.getEntrevistas(request, baseRequest);
  }

  public OperationResult<List<SelectionLabelCountDTO>> getIngresos(SelectionFilterRequest request, BaseRequest baseRequest) {
    return this.selectionRepository.getIngresos(request, baseRequest);
  }

  public OperationResult<List<SelectionPerformanceRowDTO>> getRendimiento(SelectionFilterRequest request, BaseRequest baseRequest) {
    return this.selectionRepository.getRendimiento(request, baseRequest);
  }

  public OperationResult<List<SelectionUserDTO>> getUsuarios(String filtro, BaseRequest baseRequest) {
    return this.selectionRepository.getUsuarios(filtro, baseRequest);
  }
}
