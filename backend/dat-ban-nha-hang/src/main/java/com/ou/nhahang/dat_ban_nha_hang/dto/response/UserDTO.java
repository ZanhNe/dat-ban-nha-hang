package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record UserDTO(
    Long userId,
    String username,
    String fullName,
    String email,
    String phone,
    String address,
    String avatar,
    String status,
    List<String> roles,
    String createdAt
) {}
