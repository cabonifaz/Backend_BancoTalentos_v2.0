package com.bdt.bancotalentosbackend.service;

import java.io.IOException;

import com.bdt.bancotalentosbackend.model.request.AIPromptRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.IACVResponse;

public interface IIAService {

    public IACVResponse analyzeText(String extractedText) throws IOException, InterruptedException;
    public BaseResponse prompt(AIPromptRequest request) throws IOException, InterruptedException;
}
