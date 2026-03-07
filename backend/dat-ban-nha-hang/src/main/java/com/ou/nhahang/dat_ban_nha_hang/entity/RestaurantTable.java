package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_table")
@Data
@EqualsAndHashCode(callSuper = true)
public class RestaurantTable extends Base {

    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Long capacity;

    public enum TableStatus {
        AVAILABLE,
        OCCUPIED,
        MAINTENANCE,
        PRIVATE_EVENT
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TableStatus status;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "table_area_id", nullable = false)
    private TableArea tableArea;

    @OneToMany(mappedBy = "table")
    private List<RestaurantTableSession> sessions;

    public RestaurantTable() {
    }

}
