package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.AddFavCollectionRequest;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.repository.UserRepository;
import com.bdt.bancotalentosbackend.service.IUserService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final JWTHelper jwt;

    @Override
    public BaseResponse addFavouriteCollection(String token, AddFavCollectionRequest addFavCollectionRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);

        return userRepository.addFavouriteCollection(baseRequest, addFavCollectionRequest);
    }
}
