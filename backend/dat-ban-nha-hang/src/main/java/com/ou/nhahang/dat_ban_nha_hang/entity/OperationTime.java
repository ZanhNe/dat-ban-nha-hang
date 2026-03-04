package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operation_time")
@Data
@DiscriminatorValue("OPERATION_TIME")
@PrimaryKeyJoinColumn(name = "operation_time_id")
@EqualsAndHashCode(callSuper = true)
public class OperationTime extends Time {

    @Column(name = "day", nullable = false)
    private Long day;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public OperationTime() {
    }

}
