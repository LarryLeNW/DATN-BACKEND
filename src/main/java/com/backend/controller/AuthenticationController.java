package com.backend.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.auth.*;
import com.backend.dto.request.auth.AuthenticationRequest;
import com.backend.dto.request.auth.IntrospectRequest;
import com.backend.dto.request.auth.LogoutRequest;
import com.backend.dto.request.auth.RefreshRequest;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.auth.AuthenticationResponse;
import com.backend.dto.response.auth.IntrospectResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;

    
    @PostMapping("/register")
    ApiResponse<String> register(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<String>builder()
                .result(authenticationService.register(request))
                .build();
    }
     

    @GetMapping("/{verifyToken}")
    public ApiResponse<UserResponse> verifyAccount(@PathVariable("verifyToken") @NotNull String verifyToken) throws JOSEException, ParseException {
    	return ApiResponse.<UserResponse>builder().result(authenticationService.verifyRegister(verifyToken)).build();
    }
    
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody  AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }
    

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
    	return ApiResponse.<UserResponse>builder().result(authenticationService.getMyInfo()).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
