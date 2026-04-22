package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem extends Base {

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    public enum FoodItemStatus {
        PENDING,
        SERVED,
        CANCELLED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodItemStatus status;

    @ManyToOne
    @JoinColumn(name = "food_order_id", nullable = false)
    private FoodOrder foodOrder;

    @ManyToOne
    @JoinColumn(name = "food_description_id", nullable = false)
    private FoodDescription foodDescription;

    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = true)
    private User waiter;

    @ManyToMany
    @JoinTable(name = "food_item_option", joinColumns = @JoinColumn(name = "food_item_id"), inverseJoinColumns = @JoinColumn(name = "food_option_id"))
    @Builder.Default
    private List<FoodOption> selectedOptions = new ArrayList<>();

}
