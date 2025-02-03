package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.response.ParamsListResponse;
import com.bdt.bancotalentosbackend.repository.ParamsRepository;
import com.bdt.bancotalentosbackend.service.IParamsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParamsService implements IParamsService {
    private final ParamsRepository paramsRepository;

    @Override
    public ParamsListResponse getParamsList(String groupIdMaestros) {
        return paramsRepository.paramsList(groupIdMaestros);
    }
}
