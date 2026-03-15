package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

// import java.util.ArrayList;
// import java.util.List;

import org.locationtech.jts.geom.Point;

import com.ou.nhahang.dat_ban_nha_hang.entity.Booking.BookingStatus;

@Entity
@Table(name = "restaurant")
@Data
@EqualsAndHashCode(callSuper = true)
public class Restaurant extends Base {
    @Column(name = "logo", length = 255, nullable = false)
    private String logo;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    public enum RestaurantStatus {
        OPENING,
        CLOSED,
        PENDING,
        SUSPENDED,
        REJECTED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private RestaurantStatus status;

    @Column(name = "location", columnDefinition = "POINT")
    private Point location;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "base_deposit_value", nullable = false)
    private Long baseDepositValue;

    public enum DepositType {
        FIXED,
        PER_GUEST,
        NONE
    }

    @Column(name = "deposit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DepositType depositPolicy;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    public enum CommissionType {
        PERCENTAGE,
        FIXED
    }

    @Column(name = "commission_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommissionType commissionType;

    @Column(name = "base_commission_value", nullable = false)
    private Long baseCommissionValue;

    // Restaurant – Nhân viên (1–N: 1 nhà hàng nhiều nhân viên, 1 nhân viên chỉ
    // thuộc 1 nhà hàng)
    @OneToMany(mappedBy = "workplace")
    private List<User> employees;

    // Restaurant – Manager (nhiều-một)
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    // Restaurant – TableArea (một-nhiều)
    @OneToMany(mappedBy = "restaurant")
    private List<TableArea> tableAreas;

    // Restaurant – Menu (một-nhiều)
    @OneToMany(mappedBy = "restaurant")
    private List<Menu> menus;

    // Restaurant – OperationTime (một-nhiều)
    @OneToMany(mappedBy = "restaurant")
    private List<OperationTime> operationTimes;

    // Restaurant – Booking (một-nhiều)
    @OneToMany(mappedBy = "restaurant")
    private List<Booking> bookings;

    // Restaurant – Cuisine (nhiều–nhiều)
    @ManyToMany
    @JoinTable(name = "restaurant_cuisine", joinColumns = @JoinColumn(name = "restaurant_id"), inverseJoinColumns = @JoinColumn(name = "cuisine_id"))
    private Set<Cuisine> cuisines = new HashSet<>();

    // Restaurant – LegalDoc (một-nhiều)
    @OneToMany(mappedBy = "restaurant")
    private List<LegalDoc> legalDocs;

    // Restaurant – Review (N–N qua association class: 1 nhà hàng nhiều review, 1
    // khách review nhiều nhà hàng)
    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;

    public Restaurant() {

    }

    public Booking makeBooking(User user, Restaurant restaurant, Set<RestaurantTable> tables, LocalDateTime bookingTime,
            Long numberOfPeople, String note) {
        Booking booking = Booking.builder()
                .bookingUser(user)
                .restaurant(restaurant)
                .bookingTime(new BookingTime(bookingTime, restaurant))
                .numberOfPeople(numberOfPeople)
                .note(note)
                .tables(tables)
                .build();
        return booking;
    }
}
