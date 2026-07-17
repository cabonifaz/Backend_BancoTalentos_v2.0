package com.bdt.bancotalentosbackend.model.response;

import com.bdt.bancotalentosbackend.model.dto.BlacklistValidationDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistValidateResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    private BlacklistValidationDTO validacion;
}
