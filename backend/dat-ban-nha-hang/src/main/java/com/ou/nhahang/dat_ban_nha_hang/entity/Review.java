package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "review")
@Getter
@Setter

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

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    @ToString.Exclude
    private Booking booking;

    public Review() {
    }
}
