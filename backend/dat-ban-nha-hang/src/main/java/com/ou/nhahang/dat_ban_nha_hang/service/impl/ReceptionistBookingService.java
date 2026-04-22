package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistRejectBookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistConfirmBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistRejectBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingCancelledEvent;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingConfirmedEvent;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingRejectedEvent;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IReceptionistBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReceptionistBookingService implements IReceptionistBookingService {

        private final BookingRepository bookingRepository;
        private final ApplicationEventPublisher eventPublisher;
        private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

        @Override
        @Transactional
        public ReceptionistConfirmBookingResponseDTO confirmBooking(Long bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đặt bàn"));

                if (booking.getStatus() != Booking.BookingStatus.AWAITING_CONFIRMATION) {
                        throw new IllegalStateException(
                                        "Trạng thái đặt bàn không hợp lệ để xác nhận. Hiện tại là: "
                                                        + booking.getStatus());
                }

                Long depositAmount = booking.calculateDepositAmount();

                String message;
                if (booking.getRestaurant().getDepositPolicy() == Restaurant.DepositType.NONE || depositAmount == 0L) {
                        booking.setStatus(Booking.BookingStatus.CONFIRMED);
                        message = "Yêu cầu đặt bàn của bạn đã được xác nhận thành công. Hẹn gặp bạn tại nhà hàng!";
                } else {
                        booking.setStatus(Booking.BookingStatus.PENDING_PAYMENT);
                        message = "Yêu cầu đặt bàn của bạn đã được tiếp nhận! Vui lòng thanh toán tiền cọc trong vòng 15 phút để giữ chỗ.";

                        threadPoolTaskScheduler.schedule(
                                        () -> checkPaymentExpiration(bookingId),
                                        Instant.now().plus(15, ChronoUnit.MINUTES));
                }

                booking = bookingRepository.save(booking);

                eventPublisher.publishEvent(BookingConfirmedEvent.builder()
                                .userId(booking.getBookingUser().getId())
                                .bookingId(booking.getId())
                                .customerName(booking.getBookingUser().getFullName())
                                .message(message)
                                .build());

                return ReceptionistConfirmBookingResponseDTO.builder()
                                .bookingId(booking.getId())
                                .status(booking.getStatus().name())
                                .depositAmount(depositAmount)
                                .build();
        }

        @Override
        @Transactional
        public ReceptionistRejectBookingResponseDTO rejectBooking(Long bookingId,
                        ReceptionistRejectBookingRequestDTO request) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đặt bàn"));

                if (booking.getStatus() != Booking.BookingStatus.AWAITING_CONFIRMATION) {
                        throw new IllegalStateException(
                                        "Trạng thái đặt bàn không hợp lệ để từ chối. Hiện tại là: "
                                                        + booking.getStatus());
                }

                booking.setStatus(Booking.BookingStatus.REJECTED);
                booking = bookingRepository.save(booking);

                eventPublisher.publishEvent(BookingRejectedEvent.builder()
                                .userId(booking.getBookingUser().getId())
                                .bookingId(booking.getId())
                                .customerName(booking.getBookingUser().getFullName())
                                .reason(request.reason())
                                .build());

                return ReceptionistRejectBookingResponseDTO.builder()
                                .bookingId(booking.getId())
                                .status(booking.getStatus().name())
                                .build();
        }

        @Transactional
        public void checkPaymentExpiration(Long bookingId) {
                Booking booking = bookingRepository.findById(bookingId).orElse(null);
                if (booking != null && booking.getStatus() == Booking.BookingStatus.PENDING_PAYMENT) {
                        booking.setStatus(Booking.BookingStatus.CANCELLED);
                        bookingRepository.save(booking);

                        eventPublisher.publishEvent(BookingCancelledEvent.builder()
                                        .userId(booking.getBookingUser().getId())
                                        .bookingId(booking.getId())
                                        .customerName(booking.getBookingUser().getFullName())
                                        .build());
                }
        }
}
