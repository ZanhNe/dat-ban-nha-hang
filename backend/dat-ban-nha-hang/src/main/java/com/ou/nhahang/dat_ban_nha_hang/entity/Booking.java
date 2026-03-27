package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("BOOKING")
@PrimaryKeyJoinColumn(name = "booking_id")

public class Booking extends PaymentSource {

    @Column(name = "number_of_people", nullable = false)
    private Long numberOfPeople;

    @Column(name = "note", columnDefinition = "TEXT", nullable = true)
    private String note;

    public enum BookingStatus {
        PENDING_PAYMENT,
        AWAITING_CONFIRMATION,
        CONFIRMED,
        CUSTOMER_DID_NOT_ARRIVE,
        COMPLETED,
        REJECTED,
        EXPIRED,
        CANCELLED
    }

    @Column(name = "deposit_amount", nullable = false)
    @Builder.Default
    private Long depositAmount = 0L;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING_PAYMENT;

    @ManyToOne
    @JoinColumn(name = "booking_user_id", nullable = false)
    private User bookingUser;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToOne
    @JoinColumn(name = "table_session_id", nullable = true)
    private RestaurantTableSession tableSession;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_time_id", nullable = false)
    private BookingTime bookingTime;

    @ManyToMany
    @JoinTable(name = "booking_table", joinColumns = @JoinColumn(name = "booking_id"), inverseJoinColumns = @JoinColumn(name = "table_id"))
    private Set<RestaurantTable> tables;

    @OneToOne(mappedBy = "booking")
    private Review review;

}
