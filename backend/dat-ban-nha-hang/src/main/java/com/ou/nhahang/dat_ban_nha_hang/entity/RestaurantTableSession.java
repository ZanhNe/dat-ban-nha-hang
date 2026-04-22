package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_table_session")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTableSession extends Base {

    public enum TableSessionStatus {
        ACTIVE,
        SERVING,
        PAYING,
        COMPLETED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TableSessionStatus status = TableSessionStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = false)
    private User waiter;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @OneToOne(mappedBy = "tableSession")
    private Booking booking;

    @OneToMany(mappedBy = "tableSession")
    @Builder.Default
    private List<FoodOrder> foodOrders = new ArrayList<>();

}
