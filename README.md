# Hệ Thống Đặt Bàn Nhà Hàng (Restaurant Reservation System)

## Giới thiệu dự án
Đây là hệ thống quản lý và đặt bàn nhà hàng đa nền tảng, được xây dựng nhằm số hóa quy trình vận hành nhà hàng và nâng cao trải nghiệm khách hàng. Hệ thống đóng vai trò như một cầu nối (Platform) trung gian giữa Khách hàng và các Nhà hàng đối tác.

*   **Bối cảnh:** Khách hàng thường mất nhiều thời gian gọi điện từng nhà hàng để hỏi bàn trống, đồng thời nhà hàng gặp khó khăn trong việc quản lý lịch đặt, sơ đồ bàn và kiểm soát rủi ro khách "bùng" bàn (no-show).
*   **Giải pháp:** Xây dựng hệ thống cho phép khách hàng tìm kiếm nhà hàng xung quanh qua bản đồ, xem menu chi tiết và đặt bàn trực tuyến có thanh toán cọc. Phía nhà hàng có đầy đủ công cụ quản lý sơ đồ bàn thời gian thực, gọi món tại bàn (POS) và thống kê doanh thu tự động.

---

## Đội ngũ phát triển (Nhóm 2)
| MSSV | Họ và tên | Vai trò |
| :--- | :--- | :--- |
| 2251050018 | **Diệp Bảo Doanh** | Trưởng nhóm (Leader) / Fullstack |
| 2251012111 | **Nguyễn Cao Phú** | Thành viên / Backend |
| 2251052005 | **Nguyễn Anh Cam** | Thành viên / Frontend |

---

## Công nghệ sử dụng
Hệ thống được phát triển dựa trên các công nghệ hiện đại, đảm bảo tính mở rộng và hiệu năng cao:

*   **Backend:** Java 17, Spring Boot 3 (RESTful API, Spring Security JWT, Spring Data JPA).
*   **Frontend:** ReactJS 18 (Vite), Jotai (State Management), Tailwind CSS, Recharts.
*   **Cơ sở dữ liệu:** MySQL (Dữ liệu quan hệ).
*   **Tích hợp bên thứ ba:**
    *   **Thanh toán:** Cổng thanh toán VNPay (Hỗ trợ đặt cọc và thanh toán hóa đơn).
    *   **Bản đồ:** Goong API (Tìm kiếm nhà hàng theo Geolocation).

---

## Tính năng nổi bật

Hệ thống cung cấp trải nghiệm chuyên biệt cho 3 nhóm đối tượng chính (Multi-role System):

### 1. Khách hàng (Customer)
*   Tự động định vị và tìm kiếm nhà hàng theo khoảng cách / danh mục ẩm thực (Cuisine).
*   Xem chi tiết Menu, đánh giá và cấu trúc giá cả trước khi đến.
*   Đặt bàn trực tuyến (chọn ngày, giờ, số lượng khách).
*   Thanh toán tiền cọc an toàn qua cổng VNPay.
*   Theo dõi lịch sử đơn đặt và viết Review sau khi dùng bữa.

### 2. Quản lý & Nhân viên Nhà hàng (Business)
*   **Quản lý (Manager):** Đăng ký nhà hàng mới, thiết lập sơ đồ bàn, quản lý menu (nhóm món, tùy chọn), theo dõi biểu đồ doanh thu.
*   **Lễ tân (Receptionist):** Nhận thông báo đặt bàn mới, duyệt/từ chối đơn, check-in khách đến để cập nhật sơ đồ bàn.
*   **Phục vụ (Waiter):** Order thức ăn tại bàn bằng điện thoại (POS), tùy chỉnh món ăn (Topping, Size).
*   **Thu ngân (Cashier):** Quản lý phiên thanh toán, tự động đối trừ tiền cọc, xuất hóa đơn cuối cùng.

### 3. Quản trị viên (Admin)
*   Kiểm duyệt hồ sơ đăng ký kinh doanh và giấy tờ pháp lý của nhà hàng.
*   Khóa / Mở khóa nhà hàng vi phạm.
*   Quản lý danh mục ẩm thực chuẩn của hệ thống.
*   Thiết lập và thu phí hoa hồng (Commission) theo % hoặc số tiền cố định.
*   Báo cáo tổng quan số liệu toàn hệ thống.

---

## Tài liệu tham khảo
Dự án cung cấp bộ tài liệu phân tích và thiết kế chi tiết trong thư mục `docs/`:
*   [Yêu cầu hệ thống](docs/requirements.md)
*   [Thiết kế Cơ sở dữ liệu](docs/database-design.md)
*   [Tài liệu đặc tả API](docs/api-docs.md)

---

## Cài đặt và chạy dự án (Local Development)

### Yêu cầu môi trường
*   Java Development Kit (JDK) 17+
*   Node.js 18+
*   MySQL 8.0+

### 1. Chạy Backend (Spring Boot)
Cấu hình Database trong file `application.yml` (hoặc `application.properties`), sau đó chạy lệnh:
```bash
cd backend/dat-ban-nha-hang
./mvnw clean install
./mvnw spring-boot:run
```
*API sẽ chạy tại địa chỉ:* `http://localhost:8080`

### 2. Chạy Frontend (ReactJS)
Đảm bảo đã cấu hình đúng URL API backend trong thư mục `src/services/apiClient.js` (hoặc biến môi trường), sau đó chạy lệnh:
```bash
cd frontend/dat-ban-nha-hang
npm install
npm run dev
```
*Giao diện Web sẽ chạy tại địa chỉ:* `http://localhost:5173`

---

*Hệ thống được phát triển nhằm phục vụ mục đích học thuật và đồ án tốt nghiệp/môn học. Mọi sao chép vui lòng ghi nguồn đội ngũ phát triển.*