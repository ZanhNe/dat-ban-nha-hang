package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_time")
@Data
@DiscriminatorValue("BOOKING_TIME")
@PrimaryKeyJoinColumn(name = "booking_time_id")
@EqualsAndHashCode(callSuper = true)
public class BookingTime extends Time {

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public BookingTime() {
    }

    public BookingTime(LocalDateTime bookingTime, Restaurant restaurant) {
        this.setStartTime(bookingTime);
        this.setEndTime(bookingTime.plusHours(2));
        this.restaurant = restaurant;
    }
}
