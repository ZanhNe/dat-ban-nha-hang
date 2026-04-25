package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminCuisineRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminCuisineResponseDTO;

import java.util.List;

public interface IAdminCuisineService {
    List<AdminCuisineResponseDTO> getAll();

    AdminCuisineResponseDTO create(AdminCuisineRequestDTO.Upsert request);

    AdminCuisineResponseDTO update(Long cuisineId, AdminCuisineRequestDTO.Upsert request);

    void delete(Long cuisineId);
}

