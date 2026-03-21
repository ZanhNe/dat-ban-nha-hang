package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_order")
@Getter
@Setter
@DiscriminatorValue("FOOD_ORDER")
@PrimaryKeyJoinColumn(name = "food_order_id")

public class FoodOrder extends PaymentSource {

    public enum FoodOrderStatus {
        TAKING_ORDER,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        CLOSED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodOrderStatus status;

    @ManyToOne
    @JoinColumn(name = "table_session_id", nullable = false)
    private RestaurantTableSession tableSession;

    @OneToMany(mappedBy = "foodOrder")
    private List<FoodItem> foodItems = new ArrayList<>();

    public FoodOrder() {
    }

}
