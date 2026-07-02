package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.TechSkillRequest;
import com.bdt.bancotalentosbackend.model.response.NewTechSkillReponse;
import com.bdt.bancotalentosbackend.repository.TechSkillRepository;
import com.bdt.bancotalentosbackend.service.ITechSkillService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.JWTHelper;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TechSkillService implements ITechSkillService {

    private static final Logger logger = LoggerFactory.getLogger(TechSkillService.class);
    private final JWTHelper jwt;

    @Autowired
    private TechSkillRepository techSkillRepository;

    @Override
    public NewTechSkillReponse createTechSkill(TechSkillRequest request, String token) {
        logger.info("Attempting to create a new tech skill: {}", request.getSkillName());
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, token);
        NewTechSkillReponse response = techSkillRepository.save(baseRequest, request.getSkillName());
        logger.info("Tech skill created successfully with ID: {}", request.getSkillName());
        return response;
    }
}