package com.bdt.bancotalentosbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for pre-signed URL operations (upload / download) of talent files.
 * On upload it carries {@code url}, {@code path} and {@code fileName}.
 * On download it carries {@code url} and {@code fileName} ({@code path} is null).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentPresignedUrlResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    private String url;
    private String path;
    private String fileName;
}
