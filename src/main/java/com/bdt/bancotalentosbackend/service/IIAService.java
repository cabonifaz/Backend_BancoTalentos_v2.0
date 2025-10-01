package com.bdt.bancotalentosbackend.service;

import java.io.IOException;

import com.bdt.bancotalentosbackend.model.response.IACVResponse;

public interface IIAService {

    public IACVResponse analyzeText(String extractedText) throws IOException, InterruptedException;

}
