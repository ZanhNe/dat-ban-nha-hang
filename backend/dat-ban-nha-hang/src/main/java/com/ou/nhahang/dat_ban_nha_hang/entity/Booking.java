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

    // Booking – TableSession (một–một)
    @OneToOne
    @JoinColumn(name = "table_session_id", nullable = true)
    private RestaurantTableSession tableSession;

    // Booking – BookingTime (một–một)
    @OneToOne
    @JoinColumn(name = "booking_time_id", nullable = false)
    private BookingTime bookingTime;

    // Booking – Table (nhiều–nhiều)
    @ManyToMany
    @JoinTable(
        name = "booking_table",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private java.util.Set<RestaurantTable> tables = new java.util.HashSet<>();

    /** Ràng buộc review: chỉ khách từng booking và ăn tại nhà hàng mới được review. */
    @OneToOne(mappedBy = "booking")
    private Review review;

    public Booking() {
    }

}
