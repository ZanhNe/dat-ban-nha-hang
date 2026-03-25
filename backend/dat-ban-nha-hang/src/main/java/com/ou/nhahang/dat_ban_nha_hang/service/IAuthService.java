package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.LoginRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.RegisterRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AuthResponseDTO;

public interface IAuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}
