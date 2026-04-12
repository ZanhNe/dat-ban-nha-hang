package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.*;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;

import java.util.List;

public interface IManagerTableService {
    // Table Area
    List<ManagerTableAreaResponseDTO> getTableAreas(Long restaurantId, Long managerId);

    ManagerTableAreaResponseDTO createTableArea(Long restaurantId, Long managerId,
            ManagerTableAreaRequestDTO.CreateOrUpdateTableArea requestDTO);

    ManagerTableAreaResponseDTO updateTableArea(Long tableAreaId, Long managerId,
            ManagerTableAreaRequestDTO.CreateOrUpdateTableArea requestDTO);

    void deleteTableArea(Long tableAreaId, Long managerId);

    // Table
    List<ManagerTableResponseDTO> getTables(Long tableAreaId, Long managerId);

    ManagerTableResponseDTO createTable(Long tableAreaId, Long managerId,
            ManagerTableRequestDTO.CreateOrUpdateTable requestDTO);

    ManagerTableResponseDTO updateTable(Long tableId, Long managerId,
            ManagerTableRequestDTO.CreateOrUpdateTable requestDTO);

    void deleteTable(Long tableId, Long managerId);
}
