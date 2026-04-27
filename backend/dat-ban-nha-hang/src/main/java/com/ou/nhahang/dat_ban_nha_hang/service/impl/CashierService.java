package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierCompletePaymentRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierGetServedSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierGetPayingSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import com.ou.nhahang.dat_ban_nha_hang.entity.*;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.*;
import com.ou.nhahang.dat_ban_nha_hang.service.ICashierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashierService implements ICashierService {

    private final RestaurantTableSessionRepository tableSessionRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;

    private Restaurant getWorkplace(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
        if (user.getWorkplace() == null) {
            throw new BusinessException("Nhân viên chưa được phân công nhà hàng");
        }
        return user.getWorkplace();
    }

    @Override
    public Page<CashierSessionListResponseDTO> getServedSessions(Long cashierId, CashierGetServedSessionsRequestDTO request) {
        Restaurant workplace = getWorkplace(cashierId);
        RestaurantTableSession.TableSessionStatus sessionStatus;
        try {
            sessionStatus = RestaurantTableSession.TableSessionStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            sessionStatus = RestaurantTableSession.TableSessionStatus.SERVED;
        }

        Pageable pageable = PageRequest.of(request.page(), request.limit());
        Page<RestaurantTableSession> sessions = tableSessionRepository.findByRestaurantIdAndStatus(workplace.getId(),
                sessionStatus, pageable);

        return sessions.map(session -> CashierSessionListResponseDTO.builder()
                .sessionId(session.getId())
                .tableLabels(session.getBooking().getTables().stream().map(RestaurantTable::getName).toList())
                .customerName(session.getBooking().getBookingUser().getFullName())
                .numberOfPeople(session.getBooking().getNumberOfPeople().intValue())
                .status(session.getStatus().name())
                .build());
    }

    @Override
    public Page<CashierSessionListResponseDTO> getPayingSessions(Long cashierId, CashierGetPayingSessionsRequestDTO request) {
        Restaurant workplace = getWorkplace(cashierId);
        Pageable pageable = PageRequest.of(request.page(), request.limit());
        Page<RestaurantTableSession> sessions = tableSessionRepository.findByRestaurantIdAndStatus(workplace.getId(),
                RestaurantTableSession.TableSessionStatus.PAYING, pageable);

        return sessions.map(session -> CashierSessionListResponseDTO.builder()
                .sessionId(session.getId())
                .tableLabels(session.getBooking().getTables().stream().map(RestaurantTable::getName).toList())
                .customerName(session.getBooking().getBookingUser().getFullName())
                .numberOfPeople(session.getBooking().getNumberOfPeople().intValue())
                .status(session.getStatus().name())
                .build());
    }

    @Override
    public CashierSessionDetailResponseDTO getSessionDetailForPayment(Long cashierId, Long sessionId) {
        Restaurant workplace = getWorkplace(cashierId);
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantId(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        List<CashierSessionDetailResponseDTO.CashierFoodItemSummaryDTO> items = new ArrayList<>();
        long totalFoodAmount = 0L;

        if (session.getFoodOrders() != null) {
            for (FoodOrder order : session.getFoodOrders()) {
                if (order.getStatus() == FoodOrder.FoodOrderStatus.COMPLETED && order.getFoodItems() != null) {
                    for (FoodItem item : order.getFoodItems()) {
                        if (item.getStatus() == FoodItem.FoodItemStatus.SERVED) {
                            long itemPrice = item.calculatePrice();
                            totalFoodAmount += itemPrice;
                            items.add(CashierSessionDetailResponseDTO.CashierFoodItemSummaryDTO.builder()
                                    .foodName(item.getFoodDescription().getName())
                                    .quantity(item.getQuantity())
                                    .price(itemPrice / item.getQuantity())
                                    .subtotal(itemPrice)
                                    .build());
                        }
                    }
                }
            }
        }

        return CashierSessionDetailResponseDTO.builder()
                .sessionId(session.getId())
                .tableLabels(session.getBooking().getTables().stream().map(RestaurantTable::getName).toList())
                .customerName(session.getBooking().getBookingUser().getFullName())
                .numberOfPeople(session.getBooking().getNumberOfPeople().intValue())
                .depositAmount(session.getBooking().getDepositAmount())
                .totalAmount(totalFoodAmount)
                .status(session.getStatus().name())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public CashierInitiatePaymentResponseDTO initiatePayment(Long cashierId, Long sessionId) {
        Restaurant workplace = getWorkplace(cashierId);
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantId(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        if (session.getStatus() != RestaurantTableSession.TableSessionStatus.SERVED) {
            throw new BusinessException("Chỉ có thể tạo thanh toán cho phiên bàn đã phục vụ xong (SERVED)");
        }

        long totalFoodAmount = 0L;
        if (session.getFoodOrders() != null) {
            for (FoodOrder order : session.getFoodOrders()) {
                if (order.getStatus() == FoodOrder.FoodOrderStatus.COMPLETED && order.getFoodItems() != null) {
                    for (FoodItem item : order.getFoodItems()) {
                        if (item.getStatus() == FoodItem.FoodItemStatus.SERVED) {
                            totalFoodAmount += item.calculatePrice();
                        }
                    }
                }
            }
        }

        session.setTotal(totalFoodAmount);
        session.setStatus(RestaurantTableSession.TableSessionStatus.PAYING);
        tableSessionRepository.save(session);

        long amountToPay = totalFoodAmount - session.getBooking().getDepositAmount();
        if (amountToPay < 0) {
            amountToPay = 0;
        }

        Transaction transaction = Transaction.builder()
                .amount(amountToPay)
                .transactionType(Transaction.TransactionType.FINAL_PAYMENT)
                .transactionStatus(Transaction.TransactionStatus.PENDING)
                .paymentSource(session.getBooking())
                .build();
        transactionRepository.save(transaction);

        return CashierInitiatePaymentResponseDTO.builder()
                .sessionId(session.getId())
                .status(session.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public CashierCompletePaymentResponseDTO completePayment(Long cashierId, Long sessionId,
            CashierCompletePaymentRequestDTO request) {
        Restaurant workplace = getWorkplace(cashierId);
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantId(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        if (session.getStatus() != RestaurantTableSession.TableSessionStatus.PAYING) {
            throw new BusinessException("Phiên bàn chưa khởi tạo thanh toán (PAYING)");
        }

        // Tìm giao dịch chờ thanh toán
        Transaction pendingTransaction = null;
        if (session.getBooking().getTransactions() != null) {
            for (Transaction tx : session.getBooking().getTransactions()) {
                if (tx.getTransactionType() == Transaction.TransactionType.FINAL_PAYMENT
                        && tx.getTransactionStatus() == Transaction.TransactionStatus.PENDING) {
                    pendingTransaction = tx;
                    break;
                }
            }
        }

        if (pendingTransaction == null) {
            throw new BusinessException("Không tìm thấy giao dịch chờ thanh toán cho phiên bàn này");
        }

        if (pendingTransaction.getAmount() > 0 && !request.totalAmount().equals(pendingTransaction.getAmount())) {
            throw new BusinessException("Số tiền thanh toán không khớp với số tiền cần thanh toán ("
                    + pendingTransaction.getAmount() + ")");
        }

        pendingTransaction.setTransactionStatus(Transaction.TransactionStatus.AUTHORIZED);
        transactionRepository.save(pendingTransaction);

        if (pendingTransaction.getAmount() > 0) {
            Payment.PaymentMethod method;
            try {
                method = Payment.PaymentMethod.valueOf(request.paymentMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                method = Payment.PaymentMethod.CASH;
            }

            Payment payment = Payment.builder()
                    .price(pendingTransaction.getAmount())
                    .paymentMethod(method)
                    .paymentStatus(Payment.PaymentStatus.SUCCESS)
                    .paymentType(Payment.PaymentType.PAYMENT)
                    .transaction(pendingTransaction)
                    .build();
            paymentRepository.save(payment);
        }

        session.setStatus(RestaurantTableSession.TableSessionStatus.COMPLETED);
        tableSessionRepository.save(session);

        Booking booking = session.getBooking();
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        booking.getBookingTime().setStatus(Time.TimeStatus.CLOSED);
        bookingRepository.save(booking);

        // Free the tables
        for (RestaurantTable table : booking.getTables()) {
            table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
            tableRepository.save(table);
        }

        return CashierCompletePaymentResponseDTO.builder()
                .sessionId(session.getId())
                .status(session.getStatus().name())
                .build();
    }
}
