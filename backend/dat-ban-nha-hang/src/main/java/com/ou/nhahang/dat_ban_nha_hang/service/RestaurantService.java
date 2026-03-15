package com.ou.nhahang.dat_ban_nha_hang.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ou.nhahang.dat_ban_nha_hang.dto.RestaurantTableDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantTableRepository;

@Service
public class RestaurantService {

    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantService(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    /**
     * Tương ứng với bước findTableExecute(time, quantity, resId)
     * trong sequence diagram.
     *
     * - Gọi xuống repository: findTablesValidByTimeAndRes(time, resId)
     * - Lọc thêm theo sức chứa (capacity >= quantity)
     * - Mapping sang DTO và trả về cho controller/UI.
     */
    public List<RestaurantTableDTO> findTableExecute(LocalTime time, Long quantity, Long restaurantId) {
        List<RestaurantTable> tables =
            restaurantTableRepository.findTablesValidByTimeAndRes(time, restaurantId);

        return tables.stream()
                .filter(t -> t.getCapacity() != null && t.getCapacity() >= quantity)
                .map(RestaurantTableDTO::new)
                .collect(Collectors.toList());
    }
}

