package com.ou.nhahang.dat_ban_nha_hang.event.listener;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingCancelledEvent;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingConfirmedEvent;
import com.ou.nhahang.dat_ban_nha_hang.entity.NotificationType;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.service.INotificationService;
import com.ou.nhahang.dat_ban_nha_hang.service.IUserService;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.BookingRejectedEvent;
import com.ou.nhahang.dat_ban_nha_hang.event.dto.RequestBookingEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final INotificationService notificationService;
    private final IUserService userService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        String msg = event.message() != null ? event.message()
                : "Chào " + event.customerName() + ", bạn đã đặt bàn thành công";
        notificationService.sendNotificationToUser(
                event.userId(),
                "Xác nhận đặt bàn",
                msg,
                NotificationType.BOOKING_CONFIRMED,
                Map.of("bookingId", event.bookingId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNewBookingRequest(RequestBookingEvent event) {
        List<User> receptionists = userService.getAllReceptionists();

        for (User receptionist : receptionists) {
            notificationService.sendNotificationToUser(
                    receptionist.getId(),
                    "Yêu cầu đặt bàn",
                    "Có yêu cầu đặt bàn mới từ " + event.customerName(),
                    NotificationType.NEW_BOOKING_REQUEST,
                    Map.of("bookingId", event.bookingId()));
        }

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingRejected(BookingRejectedEvent event) {
        notificationService.sendNotificationToUser(
                event.userId(),
                "Từ chối yêu cầu đặt bàn",
                "Chào " + event.customerName() + ", yêu cầu đặt bàn của bạn đã bị từ chối với lý do: " + event.reason(),
                NotificationType.BOOKING_REJECTED,
                Map.of("bookingId", event.bookingId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingCancelled(BookingCancelledEvent event) {
        notificationService.sendNotificationToUser(
                event.userId(),
                "Hủy yêu cầu đặt bàn",
                "Chào " + event.customerName()
                        + ", yêu cầu đặt bàn của bạn đã bị hủy do không thanh toán trong thời gian cho phép",
                NotificationType.BOOKING_CANCELLED,
                Map.of("bookingId", event.bookingId()));
    }
}
