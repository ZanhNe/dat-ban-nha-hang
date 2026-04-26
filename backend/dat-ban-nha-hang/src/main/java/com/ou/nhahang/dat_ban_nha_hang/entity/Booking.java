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
        AWAITING_CONFIRMATION, // Khi chờ lễ tân chấp nhận đặt bàn
        PENDING_PAYMENT, // Khi đang chờ khách thanh toán
        CONFIRMED, // Khi xác nhận đã đặt bàn
        CUSTOMER_ARRIVED, // Khi khách đã đến nhà hàng
        SERVING, // Khi nhân viên đang phục vụ
        SERVED, // Khi nhân viên đã phục vụ xong
        COMPLETED, // Khi đã hoàn thành thanh toán
        REJECTED, // Khi bị từ chối
        EXPIRED, // Khi hết thời gian chờ
        CANCELLED, // Khi bị hủy
        FAILED // Khi thanh toán thất bại
    }

    @Column(name = "deposit_amount", nullable = false)
    @Builder.Default
    private Long depositAmount = 0L;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.AWAITING_CONFIRMATION;

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

    public Long calculateDepositAmount() {
        return this.restaurant.getDepositPolicy() == Restaurant.DepositType.FIXED
                ? this.restaurant.getBaseDepositValue()
                : this.restaurant.getDepositPolicy() == Restaurant.DepositType.PER_GUEST
                        ? this.restaurant.getBaseDepositValue() * this.numberOfPeople
                        : 0L;
    }

}
