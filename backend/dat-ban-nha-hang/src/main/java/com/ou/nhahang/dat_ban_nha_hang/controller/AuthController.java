package com.ou.nhahang.dat_ban_nha_hang.controller;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.LoginRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.RegisterRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AuthResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.IAuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO responseDTO = authService.register(request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Đăng ký tài khoản thành công");
        response.put("data", responseDTO.user());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO responseDTO = authService.login(request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Đăng nhập thành công");
        response.put("data", responseDTO);
        return ResponseEntity.ok(response);
    }
}
