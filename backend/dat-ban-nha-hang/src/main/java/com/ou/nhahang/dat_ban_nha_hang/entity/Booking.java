package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking")
@Data
@DiscriminatorValue("BOOKING")
@PrimaryKeyJoinColumn(name = "booking_id")
@EqualsAndHashCode(callSuper = true)
public class Booking extends PaymentSource {

    @Column(name = "number_of_people", nullable = false)
    private Long numberOfPeople;

    @Column(name = "note", columnDefinition = "TEXT", nullable = true)
    private String note;

    public enum BookingStatus {
        AWAITING_CONFIRMATION,
        PENDING_PAYMENT,
        CONFIRMED,
        CUSTOMER_DID_NOT_ARRIVE,
        COMPLETED,
        REJECTED,
        CANCELLED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @ManyToOne
    @JoinColumn(name = "booking_user_id", nullable = false)
    private User bookingUser;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public Booking() {
    }

}
