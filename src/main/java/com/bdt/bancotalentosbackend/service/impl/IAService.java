package com.bdt.bancotalentosbackend.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.bdt.bancotalentosbackend.model.request.AIPromptRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.IACVResponse;
import com.bdt.bancotalentosbackend.model.response.PromptResponse;
import com.bdt.bancotalentosbackend.service.IIAService;
import com.bdt.bancotalentosbackend.util.ClientOpenIA;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IAService implements IIAService {

    private final ObjectMapper objectMapper;

    @Override
    public IACVResponse analyzeText(String extractedText) throws IOException, InterruptedException {
        System.out.println("Service IA started ");
        ClientOpenIA client = new ClientOpenIA();
        System.out.println("Client was created");
        String prompt = client.buildPrompt(extractedText);
        System.out.println("Prompt generated");
        String response = client.sendPrompt(prompt);
        System.out.println("Response received");

        // Mapear la respuesta JSON a un objeto IACVResponse
        IACVResponse iaCVResponse = objectMapper.readValue(response, IACVResponse.class);
        System.out.println("Response mapped to IACVResponse");
        return iaCVResponse;
    }

    @Override
    public BaseResponse prompt( AIPromptRequest request) throws IOException, InterruptedException {
        System.out.println("Service IA started ");
        ClientOpenIA client = new ClientOpenIA();
        System.out.println("Client was created");
        System.out.println("Prompt generated");
        String response = client.sendPrompt(request.getPrompt());
        System.out.println("Response received");

        // Mapear la respuesta JsonNode
        JsonNode promptResponse = objectMapper.readTree(response);
        System.out.println("Response mapped");
        return new PromptResponse(2, "Respuesta extidos", promptResponse);
    }
}
