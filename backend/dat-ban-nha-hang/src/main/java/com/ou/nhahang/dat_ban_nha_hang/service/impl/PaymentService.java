package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.PaymentInitResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Payment;
import com.ou.nhahang.dat_ban_nha_hang.entity.Transaction;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.PaymentRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.TransactionRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IPaymentService;
import com.ou.nhahang.dat_ban_nha_hang.service.port.IVNPayGateway;
import com.ou.nhahang.dat_ban_nha_hang.utils.VNPayUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

        private final BookingRepository bookingRepository;
        private final TransactionRepository transactionRepository;
        private final IVNPayGateway vnpayGateway;
        private final PaymentRepository paymentRepository;

        @Override
        @Transactional
        public PaymentInitResponseDTO initiateTransaction(Long bookingId, Long userId, String ipAddr) { // Dùng để khởi
                                                                                                        // tạo
                                                                                                        // transaction
                                                                                                        // cho thanh
                                                                                                        // toán
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Booking"));

                if (!booking.getBookingUser().getId().equals(userId)) {
                        throw new BusinessException("Bạn không có quyền để thanh toán cho booking này");
                }

                if (booking.getStatus() != Booking.BookingStatus.PENDING_PAYMENT) {
                        throw new BusinessException("Booking không ở trạng thái chờ thanh toán");
                }

                // Tính thời gian hết hạn cho thanh toán (hiện tại là 15 phút)
                LocalDateTime expireTime = booking.getUpdatedAt().plusMinutes(15);
                LocalDateTime now = LocalDateTime.now();
                long remainingSeconds = ChronoUnit.SECONDS.between(now, expireTime);

                if (remainingSeconds <= 120) {
                        throw new BusinessException(
                                        "Thời gian thanh toán đã hết hạn, vui lòng chờ 1-2 phút để hệ thống cập nhật");
                }

                Optional<Transaction> existingTx = transactionRepository
                                .findByPaymentSourceIdAndTransactionTypeAndTransactionStatusForUpdate(bookingId,
                                                Transaction.TransactionType.DEPOSIT,
                                                Transaction.TransactionStatus.PENDING);
                if (existingTx.isPresent() && existingTx.get().getPaymentUrl() != null) { // Nếu như đã tồn tại
                                                                                          // transaction
                                                                                          // thì trả về
                                                                                          // transaction đó
                        Transaction tx = existingTx.get();
                        return PaymentInitResponseDTO.builder()
                                        .url(tx.getPaymentUrl())
                                        .transactionId(tx.getId())
                                        .bookingId(bookingId)
                                        .amount(tx.getAmount())
                                        .currency("vnd")
                                        .status(tx.getTransactionStatus().name())
                                        .build();
                }

                Long amount = booking.getDepositAmount();
                if (amount == null || amount <= 0) {
                        throw new BusinessException("Số tiền đặt cọc không hợp lệ");
                }

                Transaction tx = Transaction.builder()
                                .amount(amount)
                                .transactionType(Transaction.TransactionType.DEPOSIT)
                                .transactionStatus(Transaction.TransactionStatus.PENDING)
                                .paymentSource(booking)
                                .build();

                tx = transactionRepository.saveAndFlush(tx);

                String paymentUrl = vnpayGateway.createPaymentUrl(amount, tx.getId(), expireTime.minusMinutes(2),
                                "vnd", ipAddr);

                tx.setPaymentUrl(paymentUrl);
                transactionRepository.save(tx);

                return PaymentInitResponseDTO.builder()
                                .url(paymentUrl)
                                .transactionId(tx.getId())
                                .bookingId(bookingId)
                                .amount(amount)
                                .currency("vnd")
                                .status(tx.getTransactionStatus().name())
                                .build();
        }

        @Override
        @Transactional
        public Map<String, String> handleWebhook(Map<String, String> params) {
                return vnpayGateway.handleWebhook(params);
        }

        @Override
        public List<BookingResponseDTO> getPendingBookingsForUser(Long userId) { // Dùng để lấy ra các booking của user
                                                                                 // chờ thanh toán
                List<Booking> bookings = bookingRepository.findByBookingUser_IdAndStatus(userId,
                                Booking.BookingStatus.PENDING_PAYMENT);
                return bookings.stream().map(b -> BookingResponseDTO.builder()
                                .bookingId(b.getId())
                                .restaurantId(b.getRestaurant().getId())
                                .restaurantName(b.getRestaurant().getName())
                                .bookingTime(b.getBookingTime().getStartTime())
                                .guestCount(b.getNumberOfPeople())
                                .depositAmount(b.getDepositAmount())
                                .status(b.getStatus().name())
                                .note(b.getNote())
                                .build()).collect(Collectors.toList());
        }
}
