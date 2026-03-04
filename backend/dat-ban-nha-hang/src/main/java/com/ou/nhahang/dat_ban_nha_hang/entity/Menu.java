package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu")
@Data
@EqualsAndHashCode(callSuper = true)
public class Menu extends Base {
    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    public Menu() {
    }
}
