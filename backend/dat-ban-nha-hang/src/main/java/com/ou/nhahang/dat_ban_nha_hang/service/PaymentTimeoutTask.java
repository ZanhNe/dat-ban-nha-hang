package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentTimeoutTask {

    private static final Logger logger = LoggerFactory.getLogger(PaymentTimeoutTask.class);
    private final BookingRepository bookingRepository;

    public PaymentTimeoutTask(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Chạy định kỳ 15 phút 1 lần.
     * Quét các Booking ở trạng thái PENDING_PAYMENT trong 30 phút mà chưa thanh toán để chuyển sang EXPIRED.
     */
    @Scheduled(fixedRate = 900000) // 15 mins
    @Transactional
    public void expirePendingBookings() {
        logger.info("Running PaymentTimeoutTask to expire pending bookings...");
        
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        
        List<Booking> allPending = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.PENDING_PAYMENT)
                .collect(Collectors.toList());

        int count = 0;
        for (Booking b : allPending) {
            if (b.getCreatedAt() != null && b.getCreatedAt().isBefore(thirtyMinutesAgo)) {
                b.setStatus(Booking.BookingStatus.EXPIRED);
                bookingRepository.save(b);
                count++;
            } else if (b.getCreatedAt() == null) {
                b.setStatus(Booking.BookingStatus.EXPIRED);
                bookingRepository.save(b);
                count++;
            }
        }
        
        logger.info("Expired {} pending bookings.", count);
    }
}
