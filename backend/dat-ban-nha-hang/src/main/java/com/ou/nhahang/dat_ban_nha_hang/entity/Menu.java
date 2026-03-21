package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu")
@Getter
@Setter

public class Menu extends Base {
    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menu")
    private List<FoodGroup> foodGroups = new ArrayList<>();

    public Menu() {
    }
}
