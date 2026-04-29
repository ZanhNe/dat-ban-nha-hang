# Yêu Cầu Hệ Thống Đặt Bàn Nhà Hàng (Restaurant Reservation)

## 1. Mô tả
*   **Bối cảnh:** Khách hàng muốn đặt bàn nhà hàng nhưng phải gọi điện từng nơi, không biết nhà hàng nào còn chỗ, đến nơi phải chờ đợi lâu.
*   **Giải pháp:** Xây dựng platform cho phép khách hàng tìm nhà hàng theo vị trí/cuisine, xem menu và giá, đặt bàn online với thanh toán đặt cọc. Nhà hàng quản lý menu, sơ đồ bàn, xác nhận đặt và ghi nhận order.
*   **Điểm thú vị:** Xem menu và giá trước khi đến, thanh toán đặt cọc online, đánh giá nhà hàng sau sử dụng.

## 2. Đối tượng sử dụng
*   **Khách hàng (End User):** Tìm nhà hàng, xem menu, đặt bàn, đánh giá.
*   **Nhà hàng (Business User):** Quản lý menu, bàn, xác nhận đặt, order.
*   **Admin:** Duyệt nhà hàng mới, quản lý cuisine, báo cáo.

## 3. Tính năng chính

### Khách hàng (6 tính năng)
*   Tìm kiếm nhà hàng (vị trí, cuisine)
*   Xem menu và giá
*   Đặt bàn online (ngày, giờ, số người)
*   Thanh toán đặt cọc
*   Xem lịch sử đặt bàn
*   Đánh giá nhà hàng

### Nhà hàng (6 tính năng)
*   Quản lý thông tin nhà hàng
*   Quản lý menu (CRUD món ăn)
*   Quản lý bàn (sơ đồ bàn)
*   Xác nhận/từ chối đặt bàn
*   Ghi nhận order và thanh toán
*   Báo cáo doanh thu

### Admin (6 tính năng)
*   Duyệt nhà hàng mới
*   Quản lý nhà hàng (CRUD)
*   Quản lý danh mục cuisine
*   Cấu hình commission
*   Báo cáo toàn hệ thống
*   Quản lý thông báo
