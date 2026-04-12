package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_option_group")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodOptionGroup extends Base {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private DescriptionStatus status;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToMany(mappedBy = "optionGroups")
    @Builder.Default
    private List<FoodDescription> foodDescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "optionGroup")
    @Builder.Default
    private List<FoodOption> options = new ArrayList<>();

}
