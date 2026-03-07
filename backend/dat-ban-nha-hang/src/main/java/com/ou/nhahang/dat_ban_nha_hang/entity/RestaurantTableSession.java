package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_table_session")
@Data
@EqualsAndHashCode(callSuper = true)
public class RestaurantTableSession extends Base {

    public enum TableSessionStatus {
        ACTIVE,
        CLOSED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TableSessionStatus status;

    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = false)
    private User waiter;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @OneToMany(mappedBy = "tableSession")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "tableSession")
    private List<FoodOrder> foodOrders;

    public RestaurantTableSession() {
    }
}
