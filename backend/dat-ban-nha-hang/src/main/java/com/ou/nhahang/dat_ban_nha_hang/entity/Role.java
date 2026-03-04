package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role")
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends Base {
    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String name;

    public Role() {
    }
}
