package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerTableAreaRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerTableRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerTableAreaResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerTableResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.entity.TableArea;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantTableRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.TableAreaRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerTableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerTableService implements IManagerTableService {

    private final RestaurantRepository restaurantRepository;
    private final TableAreaRepository tableAreaRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public ManagerTableService(RestaurantRepository restaurantRepository,
            TableAreaRepository tableAreaRepository,
            RestaurantTableRepository restaurantTableRepository) {
        this.restaurantRepository = restaurantRepository;
        this.tableAreaRepository = tableAreaRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    private ManagerTableAreaResponseDTO mapToTableAreaDTO(TableArea area) {
        return ManagerTableAreaResponseDTO.builder()
                .tableAreaId(area.getId())
                .name(area.getName())
                .status(area.getStatus().name())
                .build();
    }

    private ManagerTableResponseDTO mapToTableDTO(RestaurantTable table) {
        return ManagerTableResponseDTO.builder()
                .tableId(table.getId())
                .name(table.getName())
                .capacity(table.getCapacity().intValue())
                .status(table.getStatus().name())
                .build();
    }

    private Restaurant getRestaurantIfManager(Long restaurantId, Long managerId) {
        return restaurantRepository.findByIdAndManagerId(restaurantId, managerId)
                .orElseThrow(() -> new BusinessException("Bạn không có quyền quản lý nhà hàng này"));
    }

    // Table Area

    @Override
    public List<ManagerTableAreaResponseDTO> getTableAreas(Long restaurantId, Long managerId) {
        getRestaurantIfManager(restaurantId, managerId);
        return tableAreaRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToTableAreaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ManagerTableAreaResponseDTO createTableArea(Long restaurantId, Long managerId,
            ManagerTableAreaRequestDTO.CreateOrUpdateTableArea requestDTO) {
        Restaurant restaurant = getRestaurantIfManager(restaurantId, managerId);

        TableArea area = TableArea.builder()
                .name(requestDTO.name())
                .status(TableArea.TableAreaStatus.valueOf(requestDTO.status().toUpperCase()))
                .restaurant(restaurant)
                .build();

        TableArea saved = tableAreaRepository.save(area);
        return mapToTableAreaDTO(saved);
    }

    @Override
    @Transactional
    public ManagerTableAreaResponseDTO updateTableArea(Long tableAreaId, Long managerId,
            ManagerTableAreaRequestDTO.CreateOrUpdateTableArea requestDTO) {
        TableArea area = tableAreaRepository.findById(tableAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực"));
        getRestaurantIfManager(area.getRestaurant().getId(), managerId);

        area.setName(requestDTO.name());
        area.setStatus(TableArea.TableAreaStatus.valueOf(requestDTO.status().toUpperCase()));

        TableArea saved = tableAreaRepository.save(area);
        return mapToTableAreaDTO(saved);
    }

    @Override
    @Transactional
    public void deleteTableArea(Long tableAreaId, Long managerId) {
        TableArea area = tableAreaRepository.findById(tableAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực"));
        getRestaurantIfManager(area.getRestaurant().getId(), managerId);

        tableAreaRepository.delete(area);
    }

    // Table

    @Override
    public List<ManagerTableResponseDTO> getTables(Long tableAreaId, Long managerId) {
        TableArea area = tableAreaRepository.findById(tableAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực"));
        getRestaurantIfManager(area.getRestaurant().getId(), managerId);

        return restaurantTableRepository.findByTableAreaId(tableAreaId).stream()
                .map(this::mapToTableDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ManagerTableResponseDTO createTable(Long tableAreaId, Long managerId,
            ManagerTableRequestDTO.CreateOrUpdateTable requestDTO) {
        TableArea area = tableAreaRepository.findById(tableAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực"));
        getRestaurantIfManager(area.getRestaurant().getId(), managerId);

        RestaurantTable table = RestaurantTable.builder()
                .name(requestDTO.name())
                .capacity((long) requestDTO.capacity())
                .status(RestaurantTable.TableStatus.valueOf(requestDTO.status().toUpperCase()))
                .tableArea(area)
                .build();

        RestaurantTable saved = restaurantTableRepository.save(table);
        return mapToTableDTO(saved);
    }

    @Override
    @Transactional
    public ManagerTableResponseDTO updateTable(Long tableId, Long managerId,
            ManagerTableRequestDTO.CreateOrUpdateTable requestDTO) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn ăn"));
        getRestaurantIfManager(table.getTableArea().getRestaurant().getId(), managerId);

        table.setName(requestDTO.name());
        table.setCapacity((long) requestDTO.capacity());
        table.setStatus(RestaurantTable.TableStatus.valueOf(requestDTO.status().toUpperCase()));

        RestaurantTable saved = restaurantTableRepository.save(table);
        return mapToTableDTO(saved);
    }

    @Override
    @Transactional
    public void deleteTable(Long tableId, Long managerId) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn ăn"));
        getRestaurantIfManager(table.getTableArea().getRestaurant().getId(), managerId);

        restaurantTableRepository.delete(table);
    }
}
