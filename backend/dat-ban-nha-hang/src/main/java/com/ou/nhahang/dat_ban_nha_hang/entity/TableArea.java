package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "table_area")
@Data
@EqualsAndHashCode(callSuper = true)
public class TableArea extends Base {
    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    public enum TableAreaStatus {
        ACTIVE,
        CLOSED,
        MAINTENANCE,
        PRIVATE_EVENT
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TableAreaStatus status;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "tableArea")
    private List<RestaurantTable> tables;

    public TableArea() {
    }
}
