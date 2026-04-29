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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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
        RestaurantTableSession.TableSessionStatus sessionStatus = RestaurantTableSession.TableSessionStatus
                .valueOf(request.status().toUpperCase());

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

        List<CashierSessionDetailResponseDTO.CashierOrderSummaryDTO> orders = new ArrayList<>();
        long totalFoodAmount = 0L;

        if (session.getFoodOrders() != null) {
            List<FoodOrder> sortedOrders = session.getFoodOrders().stream()
                    .sorted(Comparator.comparing(FoodOrder::getCreatedAt))
                    .toList();
            for (FoodOrder order : sortedOrders) {
                List<CashierSessionDetailResponseDTO.CashierFoodItemSummaryDTO> orderItems = new ArrayList<>();
                if (order.getStatus() == FoodOrder.FoodOrderStatus.COMPLETED && order.getFoodItems() != null) {
                    for (FoodItem item : order.getFoodItems()) {
                        if (item.getStatus() == FoodItem.FoodItemStatus.SERVED) {
                            long itemPrice = item.calculatePrice();
                            totalFoodAmount += itemPrice;
                            orderItems.add(CashierSessionDetailResponseDTO.CashierFoodItemSummaryDTO.builder()
                                    .foodName(item.getFoodDescription().getName())
                                    .quantity(item.getQuantity())
                                    .price(itemPrice / item.getQuantity())
                                    .totalItemPrice(itemPrice)
                                    .build());
                        }
                    }
                }
                if (!orderItems.isEmpty()) {
                    orders.add(CashierSessionDetailResponseDTO.CashierOrderSummaryDTO.builder()
                            .orderId(order.getId())
                            .status(order.getStatus().name())
                            .createdAt(order.getCreatedAt())
                            .items(orderItems)
                            .build());
                }
            }
        }

        long amountToPay = totalFoodAmount - session.getBooking().getDepositAmount();
        if (amountToPay < 0) {
            amountToPay = 0;
        }

        return CashierSessionDetailResponseDTO.builder()
                .sessionId(session.getId())
                .tableLabels(session.getBooking().getTables().stream().map(RestaurantTable::getName).toList())
                .customerName(session.getBooking().getBookingUser().getFullName())
                .numberOfPeople(session.getBooking().getNumberOfPeople().intValue())
                .depositAmount(session.getBooking().getDepositAmount())
                .totalAmount(totalFoodAmount)
                .amountToPay(amountToPay)
                .status(session.getStatus().name())
                .orders(orders)
                .build();
    }

    @Override
    @Transactional
    public CashierInitiatePaymentResponseDTO initiatePayment(Long cashierId, Long sessionId) {
        Restaurant workplace = getWorkplace(cashierId);
        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantIdForUpdate(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        if (session.getStatus() != RestaurantTableSession.TableSessionStatus.SERVED) {
            throw new BusinessException("Chỉ có thể tạo thanh toán cho phiên bàn đã phục vụ xong (SERVED)");
        }

        List<FoodOrder> foodOrders = session.getFoodOrders() != null ? session.getFoodOrders() : List.of();
        boolean hasPendingFoodOrder = foodOrders.stream()
                .anyMatch(order -> order.getStatus() == FoodOrder.FoodOrderStatus.TAKING_ORDER
                        || order.getStatus() == FoodOrder.FoodOrderStatus.CONFIRMED);
        boolean hasPendingFoodItem = foodOrders.stream()
                .flatMap(order -> order.getFoodItems() != null ? order.getFoodItems().stream() : Stream.empty())
                .anyMatch(item -> item.getStatus() == FoodItem.FoodItemStatus.PENDING);
        if (hasPendingFoodOrder || hasPendingFoodItem) {
            throw new BusinessException("Vẫn còn món hoặc order chưa hoàn tất, chưa thể khởi tạo thanh toán");
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

        long amountToPay = totalFoodAmount - session.getBooking().getDepositAmount();
        if (amountToPay < 0) {
            amountToPay = 0;
        }

        var existedPending = transactionRepository.findByPaymentSourceIdAndTransactionTypeAndTransactionStatusForUpdate(
                session.getBooking().getId(),
                Transaction.TransactionType.FINAL_PAYMENT,
                Transaction.TransactionStatus.PENDING);
        if (existedPending.isPresent()) {
            throw new BusinessException("Phiên bàn đã có giao dịch thanh toán đang chờ xử lý");
        }

        session.setTotal(totalFoodAmount);
        session.setStatus(RestaurantTableSession.TableSessionStatus.PAYING);
        tableSessionRepository.save(session);

        Transaction transaction = Transaction.builder()
                .amount(amountToPay)
                .transactionType(Transaction.TransactionType.FINAL_PAYMENT)
                .transactionStatus(Transaction.TransactionStatus.PENDING)
                .cashier(cashier)
                .paymentSource(session.getBooking())
                .build();
        transaction = transactionRepository.save(transaction);

        return CashierInitiatePaymentResponseDTO.builder()
                .sessionId(session.getId())
                .status(session.getStatus().name())
                .transactionId(transaction.getId())
                .amountToPay(amountToPay)
                .build();
    }

    @Override
    @Transactional
    public CashierCompletePaymentResponseDTO completePayment(Long cashierId, Long sessionId,
            CashierCompletePaymentRequestDTO request) {
        Restaurant workplace = getWorkplace(cashierId);
        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantIdForUpdate(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        if (session.getStatus() != RestaurantTableSession.TableSessionStatus.PAYING) {
            throw new BusinessException("Phiên bàn chưa khởi tạo thanh toán (PAYING)");
        }

        Transaction pendingTransaction = transactionRepository
                .findByPaymentSourceIdAndTransactionTypeAndTransactionStatusForUpdate(
                        session.getBooking().getId(),
                        Transaction.TransactionType.FINAL_PAYMENT,
                        Transaction.TransactionStatus.PENDING)
                .orElseThrow(() -> new BusinessException("Không tìm thấy giao dịch chờ thanh toán cho phiên bàn này"));

        if (pendingTransaction.getAmount() > 0 && !request.totalAmount().equals(pendingTransaction.getAmount())) {
            throw new BusinessException("Số tiền thanh toán không khớp với số tiền cần thanh toán ("
                    + pendingTransaction.getAmount() + ")");
        }

        pendingTransaction.setTransactionStatus(Transaction.TransactionStatus.CAPTURED);
        pendingTransaction.setCashier(cashier);
        transactionRepository.save(pendingTransaction);

        if (pendingTransaction.getAmount() > 0) {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(request.paymentMethod().toUpperCase());

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
                .transactionId(pendingTransaction.getId())
                .build();
    }
}
