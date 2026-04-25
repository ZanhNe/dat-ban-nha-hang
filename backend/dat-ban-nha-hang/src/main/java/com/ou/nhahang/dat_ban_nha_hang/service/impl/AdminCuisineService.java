package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminCuisineRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminCuisineResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Cuisine;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.CuisineRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminCuisineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCuisineService implements IAdminCuisineService {

    private final CuisineRepository cuisineRepository;

    private AdminCuisineResponseDTO mapToDTO(Cuisine c) {
        return AdminCuisineResponseDTO.builder()
                .cuisineId(c.getId())
                .name(c.getName())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminCuisineResponseDTO> getAll() {
        return cuisineRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public AdminCuisineResponseDTO create(AdminCuisineRequestDTO.Upsert request) {
        if (cuisineRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException("Tên danh mục đã tồn tại");
        }
        Cuisine saved = cuisineRepository.save(Cuisine.builder().name(request.name()).build());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public AdminCuisineResponseDTO update(Long cuisineId, AdminCuisineRequestDTO.Upsert request) {
        Cuisine cuisine = cuisineRepository.findById(cuisineId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (!cuisine.getName().equalsIgnoreCase(request.name())
                && cuisineRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException("Tên danh mục đã tồn tại");
        }

        cuisine.setName(request.name());
        Cuisine saved = cuisineRepository.save(cuisine);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long cuisineId) {
        Cuisine cuisine = cuisineRepository.findById(cuisineId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
        cuisineRepository.delete(cuisine);
    }
}

