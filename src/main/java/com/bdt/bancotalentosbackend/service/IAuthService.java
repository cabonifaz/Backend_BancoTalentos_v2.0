package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.response.AuthResponse;

public interface IAuthService {
    AuthResponse login(String username, String password);
}
