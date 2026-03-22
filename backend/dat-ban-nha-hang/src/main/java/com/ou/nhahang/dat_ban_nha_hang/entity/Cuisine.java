package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cuisine")
@Getter
@Setter

public class Cuisine extends Base {
    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    public Cuisine() {
    }
}
