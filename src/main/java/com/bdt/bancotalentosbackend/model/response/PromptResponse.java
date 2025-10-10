package com.bdt.bancotalentosbackend.model.response;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptResponse extends BaseResponse{
    private JsonNode promptResponse;

    public PromptResponse(int code, String message, JsonNode response) {
        super(code, message);
        this.promptResponse = response;
    }
}
