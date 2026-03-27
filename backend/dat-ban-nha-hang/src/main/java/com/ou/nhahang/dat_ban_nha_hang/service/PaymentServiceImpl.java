package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.PaymentInitResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Payment;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.Transaction;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.PaymentRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.TransactionRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.port.IStripeGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final BookingRepository bookingRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final IStripeGateway stripeGateway;

    public PaymentServiceImpl(BookingRepository bookingRepository, TransactionRepository transactionRepository,
            PaymentRepository paymentRepository, UserRepository userRepository, IStripeGateway stripeGateway) {
        this.bookingRepository = bookingRepository;
        this.transactionRepository = transactionRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.stripeGateway = stripeGateway;
    }

    @Override
    @Transactional
    public PaymentInitResponseDTO initiatePayment(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Booking"));

        if (!booking.getBookingUser().getId().equals(userId)) {
            throw new BusinessException("Bạn không có quyền để thanh toán cho booking này");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING_PAYMENT) {
            throw new BusinessException("Booking không ở trạng thái chờ thanh toán");
        }

        Optional<Transaction> existingTx = transactionRepository.findByPaymentSource_IdAndTransactionType(bookingId,
                Transaction.TransactionType.DEPOSIT);
        if (existingTx.isPresent() && existingTx.get().getClientSecret() != null) {
            Transaction tx = existingTx.get();
            return PaymentInitResponseDTO.builder()
                    .clientSecret(tx.getClientSecret())
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

        IStripeGateway.PaymentGatewayIntent intent = stripeGateway.createPaymentIntent(amount, bookingId, "vnd");

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .intentId(intent.intentId())
                .clientSecret(intent.clientSecret())
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .transactionStatus(Transaction.TransactionStatus.PENDING)
                .paymentSource(booking)
                .build();
        transactionRepository.save(transaction);

        return PaymentInitResponseDTO.builder()
                .clientSecret(intent.clientSecret())
                .transactionId(transaction.getId())
                .bookingId(bookingId)
                .amount(amount)
                .currency("vnd")
                .status(transaction.getTransactionStatus().name())
                .build();
    }

    @Override
    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        String intentId = stripeGateway.extractIntentIdFromWebhook(payload, sigHeader);
        if (intentId != null) {
            Transaction tx = transactionRepository.findByIntentId(intentId)
                    .orElseThrow(
                            () -> new BusinessException("Không tìm thấy giao dịch với intent id: " + intentId));

            tx.setTransactionStatus(Transaction.TransactionStatus.AUTHORIZED);

            Booking booking = (Booking) tx.getPaymentSource();
            booking.setStatus(Booking.BookingStatus.AWAITING_CONFIRMATION);

            transactionRepository.save(tx);
            bookingRepository.save(booking);
        }
    }

    private void verifyReceptionist(Long userId, Restaurant restaurant) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isReceptionist = user.getRoles().stream().anyMatch(role -> role.getName().equals("RECEPTIONIST"));
        boolean isEmployee = user.getWorkplace() != null && user.getWorkplace().getId().equals(restaurant.getId());
        if (!isReceptionist && !isEmployee) {
            throw new BusinessException("Bạn không có quyền để phê duyệt cho booking này");
        }
    }

    @Override
    @Transactional
    public void approvePayment(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking"));

        verifyReceptionist(userId, booking.getRestaurant());

        Transaction tx = transactionRepository
                .findByPaymentSource_IdAndTransactionType(bookingId, Transaction.TransactionType.DEPOSIT)
                .orElseThrow(() -> new BusinessException("Không tìm thấy giao dịch"));

        if (tx.getTransactionStatus() != Transaction.TransactionStatus.AUTHORIZED) {
            throw new BusinessException("Giao dịch chưa được xác nhận");
        }

        stripeGateway.capturePayment(tx.getIntentId());

        tx.setTransactionStatus(Transaction.TransactionStatus.CAPTURED);
        transactionRepository.save(tx);

        Payment payment = Payment.builder()
                .price(tx.getAmount())
                .paymentType(Payment.PaymentType.PAYMENT)
                .paymentStatus(Payment.PaymentStatus.SUCCESS)
                .paymentMethod(Payment.PaymentMethod.CREDIT_CARD)
                .gatewayRef(tx.getIntentId())
                .build();
        paymentRepository.save(payment);

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void rejectPayment(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking"));

        verifyReceptionist(userId, booking.getRestaurant());

        Transaction tx = transactionRepository
                .findByPaymentSource_IdAndTransactionType(bookingId, Transaction.TransactionType.DEPOSIT)
                .orElseThrow(() -> new BusinessException("Không tìm thấy giao dịch"));

        stripeGateway.cancelPayment(tx.getIntentId());

        tx.setTransactionStatus(Transaction.TransactionStatus.CANCELLED);
        transactionRepository.save(tx);

        Payment payment = Payment.builder()
                .price(tx.getAmount())
                .paymentType(Payment.PaymentType.REFUND)
                .paymentStatus(Payment.PaymentStatus.SUCCESS)
                .paymentMethod(Payment.PaymentMethod.CREDIT_CARD)
                .gatewayRef(tx.getIntentId())
                .build();
        paymentRepository.save(payment);

        booking.setStatus(Booking.BookingStatus.REJECTED);
        bookingRepository.save(booking);
    }

    @Override
    public List<BookingResponseDTO> getPendingBookingsForUser(Long userId) {
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
