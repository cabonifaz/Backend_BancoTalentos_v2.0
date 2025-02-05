package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.AddFavCollectionRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.UserFavListResponse;
import com.bdt.bancotalentosbackend.service.impl.UserService;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/addFavourite")
    public ResponseEntity<BaseResponse> addFavouriteCollection(
            @RequestBody AddFavCollectionRequest addFavCollectionRequest,
            HttpServletRequest httpServletRequest
    ){
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = userService.addFavouriteCollection(token, addFavCollectionRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getFavourites")
    public ResponseEntity<UserFavListResponse> getFavourites(HttpServletRequest httpServletRequest) {
        UserFavListResponse response = new UserFavListResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = userService.getFavourites(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
