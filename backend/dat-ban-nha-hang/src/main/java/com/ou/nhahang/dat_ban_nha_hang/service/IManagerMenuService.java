package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.*;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;

import java.util.List;

public interface IManagerMenuService {
        // Menu
        List<ManagerMenuResponseDTO> getMenus(Long managerId);

        ManagerMenuResponseDTO createMenu(Long managerId, ManagerMenuRequestDTO.CreateOrUpdateMenu requestDTO);

        ManagerMenuResponseDTO updateMenu(Long menuId, Long managerId,
                        ManagerMenuRequestDTO.CreateOrUpdateMenu requestDTO);

        void deleteMenu(Long menuId, Long managerId);

        // Food Group
        List<ManagerFoodGroupResponseDTO> getFoodGroups(Long menuId, Long managerId);

        ManagerFoodGroupResponseDTO createFoodGroup(Long menuId, Long managerId,
                        ManagerFoodGroupRequestDTO.CreateOrUpdateFoodGroup requestDTO);

        ManagerFoodGroupResponseDTO updateFoodGroup(Long foodGroupId, Long managerId,
                        ManagerFoodGroupRequestDTO.CreateOrUpdateFoodGroup requestDTO);

        void deleteFoodGroup(Long foodGroupId, Long managerId);

        // Food
        List<ManagerFoodResponseDTO> getFoods(Long foodGroupId, Long managerId);

        ManagerFoodResponseDTO createFood(Long foodGroupId, Long managerId,
                        ManagerFoodRequestDTO.CreateOrUpdateFood requestDTO);

        ManagerFoodResponseDTO updateFood(Long foodId, Long managerId,
                        ManagerFoodRequestDTO.CreateOrUpdateFood requestDTO);

        void deleteFood(Long foodId, Long managerId);

        // Option Group
        List<ManagerOptionGroupResponseDTO> getOptionGroups(Long managerId);

        ManagerOptionGroupResponseDTO createOptionGroup(Long managerId,
                        ManagerOptionGroupRequestDTO.CreateOrUpdateOptionGroup requestDTO);

        ManagerOptionGroupResponseDTO updateOptionGroup(Long optionGroupId, Long managerId,
                        ManagerOptionGroupRequestDTO.CreateOrUpdateOptionGroup requestDTO);

        void deleteOptionGroup(Long optionGroupId, Long managerId);

        // Option
        List<ManagerOptionResponseDTO> getOptions(Long optionGroupId, Long managerId);

        ManagerOptionResponseDTO createOption(Long optionGroupId, Long managerId,
                        ManagerOptionRequestDTO.CreateOrUpdateOption requestDTO);

        ManagerOptionResponseDTO updateOption(Long optionId, Long managerId,
                        ManagerOptionRequestDTO.CreateOrUpdateOption requestDTO);

        void deleteOption(Long optionId, Long managerId);
}
