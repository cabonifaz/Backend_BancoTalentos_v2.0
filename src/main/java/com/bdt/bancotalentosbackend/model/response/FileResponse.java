package com.bdt.bancotalentosbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    private String archivo;
}
