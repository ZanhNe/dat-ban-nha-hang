package com.ou.nhahang.dat_ban_nha_hang.dto;

import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.entity.TableArea;

import lombok.Data;

@Data
public class RestaurantTableDTO {

    private Long id;
    private String name;
    private Long capacity;

    private Long tableAreaId;
    private String tableAreaName;
    private TableArea.TableAreaStatus tableAreaStatus;

    public RestaurantTableDTO() {
    }

    public RestaurantTableDTO(RestaurantTable table) {
        this.id = table.getId();
        this.name = table.getName();
        this.capacity = table.getCapacity();

        TableArea area = table.getTableArea();
        if (area != null) {
            this.tableAreaId = area.getId();
            this.tableAreaName = area.getName();
            this.tableAreaStatus = area.getStatus();
        }
    }
}

