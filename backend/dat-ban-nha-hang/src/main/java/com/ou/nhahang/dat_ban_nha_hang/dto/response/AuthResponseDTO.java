package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDTO(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    UserDTO user
) {
    public AuthResponseDTO {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }
}
