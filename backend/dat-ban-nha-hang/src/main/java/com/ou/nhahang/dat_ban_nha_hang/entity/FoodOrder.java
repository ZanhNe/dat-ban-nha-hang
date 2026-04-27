package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_order")
@Getter
@Setter
// @DiscriminatorValue("FOOD_ORDER")
// @PrimaryKeyJoinColumn(name = "food_order_id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodOrder extends Base {

    public enum FoodOrderStatus {
        TAKING_ORDER,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        CLOSED
    }

    @Column(name = "total_price", nullable = false)
    @Builder.Default
    private Long totalPrice = 0L;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodOrderStatus status;

    @ManyToOne
    @JoinColumn(name = "table_session_id", nullable = false)
    private RestaurantTableSession tableSession;

    @OneToMany(mappedBy = "foodOrder")
    @Builder.Default
    private List<FoodItem> foodItems = new ArrayList<>();

    public Long calculatePrice() {
        Long price = 0L;
        if (this.foodItems != null) {
            price = this.foodItems.stream()
                    .mapToLong(FoodItem::calculatePrice)
                    .sum();
        }
        this.totalPrice = price;
        return this.totalPrice;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
        calculatePrice();
    }

}
