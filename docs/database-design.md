# Thiết Kế Cơ Sở Dữ Liệu - Hệ Thống Đặt Bàn Nhà Hàng

Tài liệu này mô tả cấu trúc cơ sở dữ liệu của Hệ thống Đặt Bàn Nhà Hàng, được chia thành các phân hệ chức năng chính để dễ quản lý.

## 1. Phân hệ Người Dùng & Phân Quyền (User Management)
Quản lý thông tin tài khoản người dùng và hệ thống phân quyền (Role-Based Access Control).

*   **`user`**: Bảng lưu trữ thông tin chung của mọi đối tượng tham gia hệ thống (Admin, Quản lý, Nhân viên, Khách hàng).
    *   Các trường chính: `id`, `username`, `password`, `full_name`, `email`, `phone`, `status`, `workplace_restaurant_id` (nếu là nhân viên).
*   **`role`**: Danh sách các quyền trong hệ thống.
    *   Các role cơ bản: `ADMIN`, `MANAGER`, `CUSTOMER`, `RECEPTIONIST`, `WAITER`, `CASHIER`.
*   **`user_role`**: Bảng trung gian thể hiện quan hệ N-N giữa user và role.

## 2. Phân hệ Quản Lý Nhà Hàng (Restaurant Core)
Lưu trữ hồ sơ nhà hàng, danh mục ẩm thực, tài liệu pháp lý và cấu trúc không gian (khu vực, bàn).

*   **`restaurant`**: Chứa thông tin cốt lõi của nhà hàng (Tên, địa chỉ, tọa độ GPS, mức cọc, hoa hồng, trạng thái, ID quản lý).
*   **`cuisine`**: Danh mục các loại hình ẩm thực chung của hệ thống (Ví dụ: Lẩu, Nướng, Nhật Bản...).
*   **`restaurant_cuisine`**: Bảng trung gian kết nối nhà hàng và loại hình ẩm thực (N-N).
*   **`legal_doc`**: Lưu trữ các loại giấy phép kinh doanh, giấy chứng nhận an toàn thực phẩm của nhà hàng để Admin xét duyệt.
*   **`table_area`**: Phân chia không gian nhà hàng thành các khu vực (Ví dụ: Tầng 1, Tầng 2, Khu VIP).
*   **`restaurant_table`**: Danh sách bàn ăn cụ thể thuộc một khu vực, đi kèm sức chứa (capacity) và trạng thái.
*   **`time` / `operation_time`**: Quản lý khung giờ hoạt động định kỳ của nhà hàng.

## 3. Phân hệ Thực Đơn & Món Ăn (Menu & Food)
Cấu trúc quản lý thực đơn phức tạp, hỗ trợ chia nhóm và tùy chọn món ăn.

*   **`menu`**: Thực đơn tổng của nhà hàng.
*   **`food_group`**: Nhóm món ăn thuộc menu (Ví dụ: Món khai vị, Món chính, Đồ uống).
*   **`food_description`**: Thông tin chi tiết của một món ăn (Tên, giá cơ bản, hình ảnh, mô tả).
*   **`food_option_group`**: Nhóm các tùy chọn áp dụng cho món ăn (Ví dụ: Size, Mức đá, Loại Topping).
*   **`food_option`**: Các lựa chọn cụ thể bên trong group (Ví dụ: Size L (+10k), Size M (+0k), Ít đá).
*   **`food_description_option_group`**: Bảng kết nối món ăn với các nhóm tùy chọn được phép áp dụng.

## 4. Phân hệ Đặt Bàn & Phiên Phục Vụ (Booking & Table Session)
Quản lý luồng đặt bàn của khách hàng và quá trình phục vụ tại bàn.

*   **`booking`**: Lưu trữ yêu cầu đặt bàn của khách (Thời gian, số lượng người, ghi chú, số tiền cọc, trạng thái đơn, user đặt).
*   **`booking_time`**: Liên kết yêu cầu đặt bàn với khung giờ cụ thể.
*   **`booking_table`**: Lưu thông tin bàn nào được hệ thống/lễ tân gán cho đơn đặt bàn nào (N-N).
*   **`restaurant_table_session`**: (Phiên Bàn) Đại diện cho một chu trình phục vụ từ lúc khách ngồi vào bàn (Check-in) cho đến lúc thanh toán xong (Check-out). Một phiên bàn có thể được link trực tiếp từ một `booking` hợp lệ.

## 5. Phân hệ Order & Thanh Toán (Ordering & Payment)
Quản lý giỏ hàng các món ăn khách gọi tại bàn và quá trình thanh toán cọc/thanh toán hóa đơn.

*   **`food_order`**: Ghi nhận toàn bộ món ăn được gọi trong một phiên bàn (Table Session).
*   **`food_item`**: Chi tiết từng món khách gọi (Số lượng, trạng thái phục vụ: Đang làm, Đã lên món).
*   **`food_item_option`**: Lưu lại các tùy chọn cụ thể khách đã chọn cho món ăn đó (Ví dụ: Khách gọi Trà đào - Size L - Ít đá).
*   **`payment_source`**: Định danh nguồn gốc yêu cầu thanh toán (Thanh toán cọc cho Booking, hay Thanh toán hóa đơn cho Table Session).
*   **`transaction`**: Lưu vết giao dịch, bao gồm các thông tin giao tiếp với cổng thanh toán (VNPay), loại giao dịch (DEPOSIT, FINAL_PAYMENT), số tiền.
*   **`payment`**: Kết quả thanh toán cuối cùng (Thành công/Thất bại), phương thức (Tiền mặt, VNPay).

## 6. Phân hệ Bổ Trợ (Notifications & Reviews)
*   **`review`**: Ghi nhận đánh giá (rating) và bình luận của khách hàng về nhà hàng sau khi hoàn thành bữa ăn. Link trực tiếp với `booking_id` để đảm bảo tính xác thực.
*   **`notifications`**: Hệ thống thông báo tập trung.
*   **`user_read_notification`**: Đánh dấu trạng thái đã đọc của từng người dùng đối với các thông báo hệ thống.
