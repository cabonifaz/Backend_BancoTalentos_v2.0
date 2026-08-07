package com.bdt.bancotalentosbackend.model.response;

import com.bdt.bancotalentosbackend.model.dto.ParamMasterDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamMasterListResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    private List<ParamMasterDTO> registros;
    private Integer total;
}
