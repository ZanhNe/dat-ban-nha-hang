package com.ou.nhahang.dat_ban_nha_hang.entity;

public enum NotificationType {
    NEW_BOOKING_REQUEST, // Thông báo cho lễ tân có đơn mới
    BOOKING_CONFIRMED, // Lễ tân xác nhận
    PAYMENT_REMINDER, // Nhắc thanh toán
    BOOKING_REJECTED, // Lễ tân từ chối
    BOOKING_CANCELLED, // Đã bị hủy
    BOOKING_EXPIRED, // Đã hết hạn
    PAYMENT_SUCCESS, // Thanh toán thành công
    SYSTEM_MESSAGE // Thông báo hệ thống
}