package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.response.AuthResponse;
import com.bdt.bancotalentosbackend.repository.AuthRepository;
import com.bdt.bancotalentosbackend.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final AuthRepository authRepository;

    @Override
    public AuthResponse login(String username, String password) {
        return authRepository.verifyCredentials(username, password);
    }
}
