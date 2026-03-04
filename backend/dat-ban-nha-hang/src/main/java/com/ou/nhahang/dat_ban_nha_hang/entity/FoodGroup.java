package com.ou.nhahang.dat_ban_nha_hang.entity;

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

    public FoodGroup() {
    }
}
