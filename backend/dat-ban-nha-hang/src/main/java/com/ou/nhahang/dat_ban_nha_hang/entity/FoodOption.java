package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_option")
@Getter
@Setter

public class FoodOption extends Base {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private DescriptionStatus status;

    @Column(name = "price", nullable = false)
    private Long price;

    @ManyToOne
    @JoinColumn(name = "option_group_id", nullable = false)
    private FoodOptionGroup optionGroup;

    public FoodOption() {
    }
}
