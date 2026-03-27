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
        
        // Find bookings created > 30 minutes ago that are still pending payment
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        
        // Cần custom query tìm theo trạng thái hoặc lấy toàn bộ ra tự check để không sửa repository
        // Alternatively, filter in memory or add query. Wait, Booking extends PaymentSource -> Base, so we theoretically have getCreatedAt
        // Assuming we rely on getAll or add a custom query. Since custom query with date might be complex for inheritance, we can fetch all PENDING and filter.
        // Actually, since this runs regularly, the number of PENDING_PAYMENT bookings at any time should be relatively small.
        
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
                // Defensive measure if created_at is null
                b.setStatus(Booking.BookingStatus.EXPIRED);
                bookingRepository.save(b);
                count++;
            }
        }
        
        logger.info("Expired {} pending bookings.", count);
    }
}
