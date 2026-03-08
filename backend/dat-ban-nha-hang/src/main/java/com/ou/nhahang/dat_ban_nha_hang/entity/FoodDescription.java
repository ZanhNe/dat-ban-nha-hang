package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_description")
@Data
@EqualsAndHashCode(callSuper = true)
public class FoodDescription extends Base {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "image", length = 255, nullable = false)
    private String image;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private DescriptionStatus status;

    @Column(name = "price", nullable = false)
    private Long price;

    @ManyToOne
    @JoinColumn(name = "food_group_id", nullable = false)
    private FoodGroup foodGroup;

    @ManyToMany
    @JoinTable(
        name = "food_description_option_group",
        joinColumns = @JoinColumn(name = "food_description_id"),
        inverseJoinColumns = @JoinColumn(name = "food_option_group_id")
    )
    private List<FoodOptionGroup> optionGroups;

    public FoodDescription() {
    }

}
