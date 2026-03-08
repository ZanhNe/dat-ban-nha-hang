package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.List;
import java.util.Set;

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

    /** Table thuộc TableArea, TableArea thuộc Restaurant → không cần restaurant trực tiếp */
    @ManyToOne
    @JoinColumn(name = "table_area_id", nullable = false)
    private TableArea tableArea;

    @OneToMany(mappedBy = "table")
    private List<RestaurantTableSession> sessions;

    /** Table N–N Booking: 1 bàn nhiều đợt booking, 1 đợt booking chọn nhiều bàn */
    @ManyToMany(mappedBy = "tables")
    private Set<Booking> bookings;

    public RestaurantTable() {
    }

}
