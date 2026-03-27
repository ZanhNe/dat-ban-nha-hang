package com.ou.nhahang.dat_ban_nha_hang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Tìm các đăng ký đặt bàn bị trùng khung thời gian (Overlap).
     * Điều kiện overlap: (End_new > Start_check) AND (Start_new < End_check)
     * Chỉ xét các booking có trạng thái ACTIVE (như xác nhận, chờ xác nhận) 
     * của đúng nhà hàng mà khách đang muốn đặt.
     */
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.restaurant.id = :restaurantId
        AND b.status IN (
            com.ou.nhahang.dat_ban_nha_hang.entity.Booking$BookingStatus.AWAITING_CONFIRMATION,
            com.ou.nhahang.dat_ban_nha_hang.entity.Booking$BookingStatus.PENDING_PAYMENT,
            com.ou.nhahang.dat_ban_nha_hang.entity.Booking$BookingStatus.CONFIRMED
        )
        AND b.bookingTime.startTime < :requestedEndTime
        AND b.bookingTime.endTime > :requestedStartTime
    """)
    List<Booking> findOverlappingBookings(
        @Param("restaurantId") Long restaurantId,
        @Param("requestedStartTime") LocalDateTime requestedStartTime,
        @Param("requestedEndTime") LocalDateTime requestedEndTime
    );

    @Query("""
        SELECT b FROM Booking b
        WHERE b.bookingUser.id = :userId
          AND b.restaurant.id = :restaurantId
          AND b.status = com.ou.nhahang.dat_ban_nha_hang.entity.Booking$BookingStatus.COMPLETED
        ORDER BY b.createdAt DESC
    """)
    List<Booking> findCompletedBookings(
        @Param("userId") Long userId,
        @Param("restaurantId") Long restaurantId,
        org.springframework.data.domain.Pageable pageable
    );

    List<Booking> findByBookingUser_IdAndStatus(Long userId, Booking.BookingStatus status);
}
