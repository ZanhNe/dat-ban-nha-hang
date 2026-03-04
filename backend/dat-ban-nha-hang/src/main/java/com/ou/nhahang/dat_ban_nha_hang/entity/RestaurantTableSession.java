package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_table_session")
@Data
@EqualsAndHashCode(callSuper = true)
public class RestaurantTableSession extends Base {

    public enum TableSessionStatus {
        ACTIVE,
        CLOSED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TableSessionStatus status;

    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = false)
    private User waiter;

    public RestaurantTableSession() {
    }

}
