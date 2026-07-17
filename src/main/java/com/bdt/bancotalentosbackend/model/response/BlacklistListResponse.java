package com.bdt.bancotalentosbackend.model.response;

import com.bdt.bancotalentosbackend.model.dto.BlacklistItemDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistListResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    private List<BlacklistItemDTO> registros;
    private Integer total;
}
