package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "review")
@Data
@EqualsAndHashCode(callSuper = true)
public class Review extends Base {

    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT", nullable = true)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @ToString.Exclude
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    /** Ràng buộc: chỉ khách từng booking và ăn tại nhà hàng mới được review. */
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    @ToString.Exclude
    private Booking booking;

    public Review() {
    }
}
