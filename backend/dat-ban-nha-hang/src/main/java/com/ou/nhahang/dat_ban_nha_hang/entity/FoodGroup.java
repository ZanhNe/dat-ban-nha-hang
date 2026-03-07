package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_group")
@Data
@EqualsAndHashCode(callSuper = true)
public class FoodGroup extends Base {
    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @OneToMany(mappedBy = "foodGroup")
    private List<FoodDescription> foodDescriptions;

    public FoodGroup() {
    }
}
