package com.bdt.bancotalentosbackend.model.response;

import com.bdt.bancotalentosbackend.model.dto.ParamsDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ParamsListResponse {
    @JsonProperty("result")
    private BaseResponse response;
    private List<ParamsDTO> paramsList;
}
