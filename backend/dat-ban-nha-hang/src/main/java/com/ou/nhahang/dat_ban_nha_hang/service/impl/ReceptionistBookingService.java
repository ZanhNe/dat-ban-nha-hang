package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistGetBookingsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistGetAwaitingBookingsRequestDTO;
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
import java.util.List;
import java.util.Optional;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistCancelBookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistBookingListResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistBookingDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistCancelBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistCheckInResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTableSession;
import com.ou.nhahang.dat_ban_nha_hang.entity.Time;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantTableRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantTableSessionRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceptionistBookingService implements IReceptionistBookingService {

        private final BookingRepository bookingRepository;
        private final TransactionRepository transactionRepository;
        private final RestaurantTableSessionRepository tableSessionRepository;
        private final RestaurantTableRepository tableRepository;
        private final UserRepository userRepository;
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

        private Restaurant getWorkplace(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
                if (user.getWorkplace() == null) {
                        throw new BusinessException("Nhân viên chưa được phân công nhà hàng");
                }
                return user.getWorkplace();
        }

        @Override
        public Page<ReceptionistBookingListResponseDTO> getBookings(Long userId, ReceptionistGetBookingsRequestDTO request) {
                Restaurant workplace = getWorkplace(userId);
                Booking.BookingStatus bookingStatus;
                try {
                        bookingStatus = Booking.BookingStatus.valueOf(request.status().toUpperCase());
                } catch (IllegalArgumentException e) {
                        bookingStatus = Booking.BookingStatus.CONFIRMED;
                }

                String[] sortParams = request.sort().split(",");
                String sortBy = sortParams[0];
                Sort.Direction sortDirection = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(request.page(), request.limit(), Sort.by(sortDirection, sortBy));

                Page<Booking> bookings = bookingRepository.findByRestaurant_IdAndStatus(workplace.getId(),
                                bookingStatus, pageable);

                return bookings.map(booking -> ReceptionistBookingListResponseDTO.builder()
                                .bookingId(booking.getId())
                                .customerName(booking.getBookingUser().getFullName())
                                .customerPhone(booking.getBookingUser().getPhone())
                                .bookingTime(booking.getBookingTime().getStartTime())
                                .numberOfPeople(booking.getNumberOfPeople().intValue())
                                .status(booking.getStatus().name())
                                .build());
        }

        @Override
        public Page<ReceptionistBookingListResponseDTO> getAwaitingBookings(Long userId, ReceptionistGetAwaitingBookingsRequestDTO request) {
                Restaurant workplace = getWorkplace(userId);
                Pageable pageable = PageRequest.of(request.page(), request.limit(), Sort.by(Sort.Direction.ASC, "bookingTime.startTime"));

                Page<Booking> bookings = bookingRepository.findByRestaurant_IdAndStatus(workplace.getId(),
                                Booking.BookingStatus.AWAITING_CONFIRMATION, pageable);

                return bookings.map(booking -> ReceptionistBookingListResponseDTO.builder()
                                .bookingId(booking.getId())
                                .customerName(booking.getBookingUser().getFullName())
                                .customerPhone(booking.getBookingUser().getPhone())
                                .bookingTime(booking.getBookingTime().getStartTime())
                                .numberOfPeople(booking.getNumberOfPeople().intValue())
                                .status(booking.getStatus().name())
                                .build());
        }

        @Override
        public ReceptionistBookingDetailResponseDTO getBookingDetail(Long userId, Long bookingId) {
                Restaurant workplace = getWorkplace(userId);
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đặt bàn"));

                if (!booking.getRestaurant().getId().equals(workplace.getId())) {
                        throw new BusinessException("Không có quyền truy cập yêu cầu đặt bàn của nhà hàng khác");
                }

                return ReceptionistBookingDetailResponseDTO.builder()
                                .bookingId(booking.getId())
                                .customer(ReceptionistBookingDetailResponseDTO.CustomerDTO.builder()
                                                .userId(booking.getBookingUser().getId())
                                                .fullName(booking.getBookingUser().getFullName())
                                                .phone(booking.getBookingUser().getPhone())
                                                .build())
                                .bookingTime(booking.getBookingTime().getStartTime())
                                .numberOfPeople(booking.getNumberOfPeople().intValue())
                                .note(booking.getNote())
                                .depositAmount(booking.getDepositAmount())
                                .status(booking.getStatus().name())
                                .assignedTables(booking.getTables().stream()
                                                .map(table -> ReceptionistBookingDetailResponseDTO.AssignedTableDTO
                                                                .builder()
                                                                .tableId(table.getId())
                                                                .label(table.getName())
                                                                .build())
                                                .toList())
                                .build();
        }

        @Override
        @Transactional
        public ReceptionistCheckInResponseDTO checkInBooking(Long userId, Long bookingId) {
                Restaurant workplace = getWorkplace(userId);
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đặt bàn"));

                if (!booking.getRestaurant().getId().equals(workplace.getId())) {
                        throw new BusinessException("Không có quyền truy cập yêu cầu đặt bàn của nhà hàng khác");
                }

                if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
                        throw new BusinessException("Yêu cầu đặt bàn không ở trạng thái xác nhận");
                }

                // Cập nhật trạng thái đơn đặt bàn thành khách đã đến
                booking.setStatus(Booking.BookingStatus.CUSTOMER_ARRIVED);

                // Tạo phiên làm việc (waiter_id ban đầu để trống là chưa có phục vụ đảm nhận)
                RestaurantTableSession session = RestaurantTableSession.builder()
                                .booking(booking)
                                .build();

                if (booking.getTables().isEmpty()) {
                        throw new BusinessException("Yêu cầu đặt bàn này chưa được xếp bàn, không thể check-in");
                }

                session = tableSessionRepository.save(session);

                booking.setTableSession(session);

                List<Long> tableIds = booking.getTables().stream().map(table -> {
                        table.setStatus(RestaurantTable.TableStatus.OCCUPIED);
                        tableRepository.save(table);
                        return table.getId();
                }).toList();

                bookingRepository.save(booking);

                return ReceptionistCheckInResponseDTO.builder()
                                .sessionId(session.getId())
                                .tableIds(tableIds)
                                .status(session.getStatus().name())
                                .build();
        }

        @Override
        @Transactional
        public ReceptionistCancelBookingResponseDTO cancelBooking(Long userId, Long bookingId,
                        ReceptionistCancelBookingRequestDTO request) {
                Restaurant workplace = getWorkplace(userId);
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đặt bàn"));

                if (!booking.getRestaurant().getId().equals(workplace.getId())) {
                        throw new BusinessException("Không có quyền truy cập yêu cầu đặt bàn của nhà hàng khác");
                }

                if (booking.getStatus() == Booking.BookingStatus.COMPLETED
                                || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                        throw new BusinessException("Yêu cầu đặt bàn đã hoàn tất hoặc đã hủy");
                }

                booking.setStatus(Booking.BookingStatus.CANCELLED);
                booking.getBookingTime().setStatus(Time.TimeStatus.CLOSED);

                bookingRepository.save(booking);

                return ReceptionistCancelBookingResponseDTO.builder()
                                .bookingId(booking.getId())
                                .status(booking.getStatus().name())
                                .build();
        }
}
