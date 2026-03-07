package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_option_group")
@Data
@EqualsAndHashCode(callSuper = true)
public class FoodOptionGroup extends Base {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private DescriptionStatus status;

    @ManyToOne
    @JoinColumn(name = "food_description_id", nullable = false)
    private FoodDescription foodDescription;

    @OneToMany(mappedBy = "optionGroup")
    private List<FoodOption> options;

    public FoodOptionGroup() {
    }

}
