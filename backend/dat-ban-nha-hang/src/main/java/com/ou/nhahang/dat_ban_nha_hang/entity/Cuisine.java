package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cuisine")
@Data
@EqualsAndHashCode(callSuper = true)
public class Cuisine extends Base {
    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    public Cuisine() {
    }
}
