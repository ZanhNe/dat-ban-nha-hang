package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistRejectBookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistConfirmBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistRejectBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.Transaction;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingExpiredEvent;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingConfirmedEvent;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingRejectedEvent;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.TransactionRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IReceptionistBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceptionistBookingService implements IReceptionistBookingService {

        private final BookingRepository bookingRepository;
        private final TransactionRepository transactionRepository;
        private final ApplicationEventPublisher eventPublisher;
        private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

        @Override
        @Transactional
        public ReceptionistConfirmBookingResponseDTO confirmBooking(Long bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đặt bàn"));

                if (booking.getStatus() != Booking.BookingStatus.AWAITING_CONFIRMATION) {
                        throw new BusinessException("Yêu cầu đặt bàn hiện tại không ở trạng thái chờ xác nhận");
                }

                Long depositAmount = booking.getDepositAmount();

                String message;
                if (booking.getRestaurant().getDepositPolicy() == Restaurant.DepositType.NONE) {
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
                        throw new BusinessException("Yêu cầu đặt bàn hiện tại không ở trạng thái chờ xác nhận");
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
                log.info("Đang tìm kiếm giao dịch tiền cọc cho Booking ID: {}", bookingId);
                Optional<Transaction> tx = transactionRepository
                                .findByPaymentSourceIdAndTransactionTypeAndTransactionStatusForUpdate(bookingId,
                                                Transaction.TransactionType.DEPOSIT,
                                                Transaction.TransactionStatus.PENDING);

                log.info("Đang tìm kiếm yêu cầu đặt bàn ID: {}", bookingId);
                Optional<Booking> bk = bookingRepository.findByIdForUpdate(bookingId);

                if (bk.isPresent() && bk.get().getStatus() == Booking.BookingStatus.PENDING_PAYMENT) {
                        bk.get().setStatus(Booking.BookingStatus.EXPIRED);
                        bookingRepository.save(bk.get());
                        log.debug("Đã hủy yêu cầu đặt bàn ID: {}", bookingId);

                        eventPublisher.publishEvent(BookingExpiredEvent.builder()
                                        .userId(bk.get().getBookingUser().getId())
                                        .bookingId(bk.get().getId())
                                        .customerName(bk.get().getBookingUser().getFullName())
                                        .build());
                }

                if (tx.isPresent()) {
                        log.info("Scheduler: Phát hiện giao dịch cho Booking ID: {} hết hạn thanh toán. Đang tiến hành hủy...",
                                        bookingId);
                        tx.get().setTransactionStatus(Transaction.TransactionStatus.EXPIRED);
                        transactionRepository.save(tx.get());

                        log.debug("Đã hủy giao dịch tiền cọc ID: {}", tx.get().getId());

                }
        }
}
