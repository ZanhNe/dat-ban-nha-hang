# API Contract - Hệ thống Đặt bàn Nhà hàng

> **Base URL:** `http://localhost:8080/api/v1`
> **Version:** 1.0.0
> **Last Updated:** 2026-02-15

---

## Mục lục

- [1. Quy ước chung](#1-quy-ước-chung)
- [2. Authentication](#2-authentication)
- [3. Customer API](#3-customer-api)
- [4. Module: Manager API](#4-module-manager-api)
- [5. Module: Notification](#5-module-notification)
- [6. Module: Receptionist Booking](#6-module-receptionist-booking)
- [7. Module: Admin API](#7-module-admin-api)
- [8. State Machine](#8-state-machine)
- [9. Module: Waiter API](#9-module-waiter-api)
- [10. Module: Cashier API](#10-module-cashier-api)

---

## 1. Quy ước chung

### 1.1. HTTP Methods

| Method   | Mục đích                      |
| -------- | ----------------------------- |
| `GET`    | Lấy dữ liệu                  |
| `POST`   | Tạo mới tài nguyên            |
| `PUT`    | Cập nhật toàn bộ tài nguyên   |
| `PATCH`  | Cập nhật một phần tài nguyên  |
| `DELETE` | Xóa tài nguyên                |

### 1.2. HTTP Status Codes

| Code  | Ý nghĩa                                          |
| ----- | ------------------------------------------------- |
| `200` | Thành công                                        |
| `201` | Tạo mới thành công                                |
| `204` | Thành công, không có nội dung trả về              |
| `400` | Yêu cầu không hợp lệ (Bad Request)               |
| `401` | Chưa xác thực (Unauthorized)                      |
| `403` | Không có quyền truy cập (Forbidden)               |
| `404` | Không tìm thấy tài nguyên (Not Found)             |
| `409` | Xung đột dữ liệu (Conflict)                      |
| `422` | Dữ liệu không thể xử lý (Unprocessable Entity)   |
| `500` | Lỗi máy chủ (Internal Server Error)               |

### 1.3. Response Format chuẩn

**Thành công (Success):**

```json
{
  "status": 200,
  "message": "Mô tả kết quả",
  "data": { },
  "meta": { } // Chứa những thông tin bổ sung liên quan đến data
}
```

**Thành công với phân trang (Paginated):**

```json
{
  "status": 200,
  "message": "Mô tả kết quả",
  "data": {},
  "meta": {
    "page": 2,          // Trang hiện tại
    "limit": 10,        // Số lượng item/trang
    "totalItems": 505, // Tổng số bản ghi trong DB (để tính ra số trang cuối)
    "totalPages": 51   // Tổng số trang (Backend tính sẵn hộ Frontend luôn)
  }
}
```

**Lỗi (Error):**

```json
{
  "status": 400,
  "message": "Mô tả lỗi chung",
  "errors": {
    "fieldName": "Mô tả lỗi cụ thể cho field"
  }
}
```

### 1.4. Quy ước đặt tên

- **URL:** dùng `kebab-case` và danh từ số nhiều → `/api/v1/dish-categories`
- **Request/Response body:** dùng `camelCase` → `firstName`, `tableNumber`
- **Query params:** dùng `camelCase` → `?pageSize=10&sortBy=createdAt`

### 1.5. Authentication Header

```
Authorization: Bearer <JWT_TOKEN>
```

---

## 2. Authentication

### 2.1. Đăng nhập

**`POST /auth/login`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Đăng nhập vào hệ thống     |
| **Auth**     | Không yêu cầu           |
| **Role**     | Tất cả                     |

**Request Body:**

```json
{
  "username": "string, required",
  "password": "string, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJl...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": 1,
      "username": "admin",
      "fullName": "Nguyễn Văn A",
      "email": "admin@example.com",
      "phone": "0987654321",
      "avatar": "https://abc.com/avatar.jpg",
      "roles": ["ADMIN"]
    }
  }
}
```

**Response `401 Unauthorized`:**

```json
{
  "status": 401,
  "message": "Sai tên đăng nhập hoặc mật khẩu",
  "errors": null
}
```

### 2.2. Đăng ký

**`POST /auth/register`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Đăng ký tài khoản người dùng |
| **Auth**     | Không yêu cầu              |
| **Role**     | Tất cả                     |

**Request Body:**

```json
{
  "username": "string, required",
  "password": "string, required, tối thiểu 6 ký tự",
  "fullName": "string, required",
  "email": "string, required, định dạng email hợp lệ",
  "phone": "string, required",
  "address": "string, optional"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Đăng ký tài khoản thành công",
  "data": {
    "userId": 2,
    "username": "nguyenvana",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phone": "0901234567",
    "address": "123 Đường A, Quận 1, TP.HCM",
    "avatar": null,
    "status": "ACTIVE",
    "roles": ["CUSTOMER"],
    "createdAt": "2026-03-25T10:00:00"
  }
}
```

**Response `400 Bad Request`:**
(Lỗi do sai định dạng đầu vào hoặc vi phạm logic kinh doanh)

```json
{
  "status": 400,
  "message": "Tên đăng nhập đã tồn tại / Dữ liệu không hợp lệ",
  "errors": null // Hoặc object chứa chi tiết lỗi field nếu sai định dạng (@NotBlank, @Email...)
}
```


---


## 3. Customer API

### 3.1. Lấy tọa độ

**`GET /api/v1/geolocation/get-coordinates`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Dùng để lấy tọa độ của một địa chỉ cụ thể           |
| **Auth**     | Không|
| **Role**     | Guest           |

**Query Parameters:**

| Param    | Type      | Required | Default | Mô tả           |
| -------- | --------- | -------- | ------- | ---------------- |
| `address`   | `string` | Yes       | `null`    | Địa chỉ do khách hàng đưa ra |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": {
    "geometry": {
      "location": {
        "latitude": 10.758,
        "longitude": 106.663
      }
    }
  },      
} 
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```


### 3.2. Tìm kiếm nhà hàng 

**`GET /api/v1/restaurants`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Dùng để tìm kiếm các nhà hàng dựa vào thông tin được cung cấp           |
| **Auth**     | Không|
| **Role**     | Guest           |

**Query Parameters:**

| Param    | Type      | Required | Default | Mô tả           |
| -------- | --------- | -------- | ------- | ---------------- |
| `page`   | `integer` | No       | `0`     | Số trang         |
| `limit`   | `integer` | No       | `10`    | Số phần tử/trang |
| `origin`   | `string` | No       | `null`    | Tọa độ của khách hàng (latitude,longitude) |
| `cuisine`   | `string` | No       | `null`    | Loại hình ẩm thực |
| `radius`   | `integer` | No       | `2`    | Bán kính tìm kiếm |


**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": [ //Dữ liệu chính hoặc Array

    {
    "restaurantId": 101,
    "restaurantName": "Haidilao - Chi nhánh Hùng Vương Plaza",
    "restaurantLogo": "https://abc.com/logo.png",
    "restaurantCuisines": ["Lẩu", "Món Trung"],
    "restaurantAvgRating": 4.8,
    "restaurantTotalReviews": 1250,
    "restaurantAddress": "126 Hùng Vương, Quận 5, TP.HCM",
    "restaurantDistance": 1.2, // km (Được tính từ tọa độ của khách hàng so với tọa độ của nhà hàng)
    "restaurantIsOpen": true, // (Tính toán giữa thời gian thực tế và thời gian mở cửa của nhà hàng)
    "restaurantLocation": {
      "latitude": 10.758,
      "longitude": 106.663
    }
    },
  ],      
  "meta": {
    "page": 2,          // Trang hiện tại
    "limit": 10,        // Số lượng item/trang
    "totalItems": 505, // Tổng số bản ghi trong DB (để tính ra số trang cuối)
    "totalPages": 51   // Tổng số trang (Backend tính sẵn hộ Frontend luôn)
  },      // Metadata (phân trang, tổng số dòng - optional)
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

### 3.3. Lấy chi tiết nhà hàng

**`GET /api/v1/restaurants/{id}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy thông tin chi tiết của một nhà hàng cụ thể dựa vào ID.            |
| **Auth**     | No |
| **Role**     | Guest           |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `id`  | `integer` | Yes       | ID của nhà hàng |

**Query Parameters:**

| Param    | Type      | Required | Default | Mô tả           |
| -------- | --------- | -------- | ------- | ---------------- |
| `origin`   | `string` | Yes       | `null`    | Tọa độ của khách hàng (latitude, longitude) |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": {
    "restaurantId": 101,
    "restaurantName": "Haidilao - Chi nhánh Hùng Vương Plaza",
    "restaurantImage": "https://abc.com/logo.png",
    "restaurantLogo": "https://abc.com/logo.png",
    "restaurantDescription": "Thương hiệu lẩu nổi tiếng với dịch vụ chăm sóc khách hàng tận tâm...",
    "restaurantAddress": "126 Hùng Vương, Quận 5, TP.HCM",
    "restaurantLocation": {
      "latitude": 10.758,
      "longitude": 106.663
    },
    "restaurantCuisines": ["Lẩu", "Món Trung"],
    "restaurantAvgRating": 4.8,
    "restaurantTotalReviews": 1250,
    "restaurantDepositPolicy": "Fixed",
    "restaurantBaseDeposit": 200000,
    "restaurantOperationTimes": [
      { "day": "Monday", "open": "09:00", "close": "23:00" },
      { "day": "Tuesday", "open": "09:00", "close": "23:00" }
    ],
    "restaurantTableAreas": [
      { "areaName": "Trong nhà", "availableTables": 15, "maxCapacity": 10 },
      { "areaName": "VIP", "availableTables": 2, "maxCapacity": 20 }
    ],
    "restaurantDirections": {} //Dùng để chứa thông tin chỉ đường từ vị trí của khách hàng đến nhà hàng
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```


---

### 3.4. Xem menu nhà hàng
**`GET /api/v1/restaurants/{id}/menu`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách menu của nhà hàng dựa vào ID.            |
| **Auth**     | No |
| **Role**     | Guest           |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `id`  | `integer` | Yes       | ID của nhà hàng |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": {
    "restaurantId": 101,
    "restaurantName": "Haidilao - Chi nhánh Hùng Vương Plaza",
    "restaurantMenus": [
      {
        "menuId": 1,
        "menuName": "Menu 1",
        "menuDescription": "Menu 1",
        "restaurantMenu": [
          {
            "groupId": 1,
            "groupName": "Khai vị",
            "groupDescription": "Món khai vị",
            "items": [
              {
                "itemId": 1,
                "itemName": "Gỏi cuốn",
                "itemDescription": "Cuốn rau sống tươi ngon",
                "itemPrice": 50000,
                "itemImage": "https://abc.com/logo.png"
              },
              {
                "itemId": 2,
                "itemName": "Chả giò",
                "itemDescription": "Chả giò giòn rụm",
                "itemPrice": 60000,
                "itemImage": "https://abc.com/logo.png"
              }
            ]
          },
          {
            "groupId": 2,
            "groupName": "Món chính",
            "groupDescription": "Món chính",
            "items": [
              {
                "itemId": 3,
                "itemName": "Lẩu Thái",
                "itemDescription": "Lẩu Thái chua cay",
                "itemPrice": 200000,
                "itemImage": "https://abc.com/logo.png"
              },
              {
                "itemId": 4,
                "itemName": "Cơm chiên",
                "itemDescription": "Cơm chiên hải sản",
                "itemPrice": 100000,
                "itemImage": "https://abc.com/logo.png"
              }
            ]
          }
        ]
      },
      {
        "menuId": 2,
        "menuName": "Menu 2",
        "menuDescription": "Menu 2",
        "restaurantMenu": [
          {
            "groupId": 1,
            "groupName": "Khai vị",
            "groupDescription": "Món khai vị",
            "items": [
              {
                "itemId": 1,
                "itemName": "Gỏi cuốn",
                "itemDescription": "Cuốn rau sống tươi ngon",
                "itemPrice": 50000,
                "itemImage": "https://abc.com/logo.png"
              },
              {
                "itemId": 2,
                "itemName": "Chả giò",
                "itemDescription": "Chả giò giòn rụm",
                "itemPrice": 60000,
                "itemImage": "https://abc.com/logo.png"
              }
            ]
          },
          {
            "groupId": 2,
            "groupName": "Món chính",
            "groupDescription": "Món chính",
            "items": [
              {
                "itemId": 3,
                "itemName": "Lẩu Thái",
                "itemDescription": "Lẩu Thái chua cay",
                "itemPrice": 200000,
                "itemImage": "https://abc.com/logo.png"
              },
              {
                "itemId": 4,
                "itemName": "Cơm chiên",
                "itemDescription": "Cơm chiên hải sản",
                "itemPrice": 100000,
                "itemImage": "https://abc.com/logo.png"
              }
            ]
          }
        ]
      }
    ],
  
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```


---

### 3.5. Xem chi tiết danh sách bàn cho việc đặt bàn
**`GET /api/v1/restaurants/{id}/tables`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách bàn của nhà hàng dựa vào thời gian đặt            |
| **Auth**     | Yes (Bearer Token) |
| **Role**     | Customer           |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `id`  | `integer` | Yes       | ID của nhà hàng |


**Query Parameters:**

| Param    | Type      | Required | Default | Mô tả           |
| -------- | --------- | -------- | ------- | ---------------- |
| `date`   | `string` | Yes       | `null`     | Ngày cần tìm kiếm         |
| `time`   | `string` | Yes       | `null`    | Thời gian cần tìm kiếm |
| `guests`   | `integer` | Yes       | `null`    | Số lượng khách cần tìm kiếm |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": {
    "restaurantId": 101,
    "requestContext": {
      "dateTime": "2026-02-15T19:00:00",
      "guests": 4
    },
    "areas": [
    {
      "areaName": "Sân thượng",
      "tables": [
        {
          "tableId": 1,
          "label": "Bàn 01",
          "capacity": 4,
          "isAvailable": true, // Backend đã tính toán dựa trên Booking/Session
          "reason": null 
        },
        {
          "tableId": 2,
          "label": "Bàn 02",
          "capacity": 2,
          "isAvailable": false,
          "reason": "ABC" // Bàn này đã có khách đang ăn
        },
        {
          "tableId": 3,
          "label": "Bàn 03",
          "capacity": 4,
          "isAvailable": false,
          "reason": "ALREADY_BOOKED" // Bàn này đã có người đặt lúc 19h
        }
      ]
    },
    {
      "areaName": "Trong nhà",
      "tables": [
        {
          "tableId": 1,
          "label": "Bàn 01",
          "capacity": 4,
          "isAvailable": true, // Backend đã tính toán dựa trên Booking/Session
          "reason": null 
        },
        {
          "tableId": 2,
          "label": "Bàn 02",
          "capacity": 2,
          "isAvailable": false,
          "reason": "ABC" // Bàn này đã có khách đang ăn
        },
        {
          "tableId": 3,
          "label": "Bàn 03",
          "capacity": 4,
          "isAvailable": false,
          "reason": "ALREADY_BOOKED" // Bàn này đã có người đặt lúc 19h
        }
      ]
    }
    ]
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```
### 3.6. Tạo thanh toán qua Stripe
**`POST /api/v1/payments/create-intent`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Tạo một payment intent cho thanh toán qua Stripe            |
| **Auth**     | Bearer Token |
| **Role**     | Customer           |


**Request Body:**
```json
{
  "order_id": 1054,             // (Int/UUID, Required) Mã đơn hàng đã được tạo trước đó trong DB
  "currency": "vnd"             // (String, Optional) Mặc định Backend có thể set cứng là 'vnd' hoặc 'usd'
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Payment intent created successfully",
  "data": {
    "client_secret": "pi_3Mtw..._secret_...xyz", // (String) Chuỗi bí mật Stripe cấp, FE nạp vào thư viện Stripe.js
    "payment_intent_id": "pi_3Mtw...",          // (String) ID của phiên giao dịch trên hệ thống Stripe
    "amount": 500000,                           // (Int) Tổng tiền BE đã tính toán (Trả về để FE hiển thị xác nhận lại cho khách)
    "currency": "vnd"
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

HTTP Code,Error Code,Mô tả
404,ORDER_NOT_FOUND,Không tìm thấy order_id trong hệ thống.
400,ORDER_ALREADY_PAID,Đơn hàng này đã được thanh toán rồi (chặn user bấm thanh toán 2 lần).
400,ORDER_EMPTY,"Đơn hàng không có món nào (tổng tiền = 0), không thể gọi Stripe."
502,STRIPE_GATEWAY_ERROR,Lỗi kết nối với server của Stripe (Stripe bị sập hoặc sai API Key).

### 3.7. Xác nhận thanh toán qua Stripe
**`POST /api/v1/webhooks/stripe`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xác nhận thanh toán qua Stripe            |
| **Auth**     | No Auth |
| **Role**     | No Auth           |


1. Headers (Cực kỳ quan trọng để bảo mật)
Stripe sẽ gửi kèm một Header tên là Stripe-Signature. Bạn BẮT BUỘC phải dùng thư viện của Stripe để kiểm tra xem request này có đúng là do Stripe gửi không, hay là của một hacker đang cố tình gọi API của bạn để hack trạng thái "Đã thanh toán".

2. Body
Stripe gửi một cấu trúc Event rất to, nhưng chỉ cần quan tâm đến 2 trường cốt lõi: type (Loại sự kiện) và data.object (Dữ liệu chi tiết).

**Request Body:**
```json
{
  "id": "evt_3Mtw...",
  "type": "payment_intent.succeeded", // <-- Quan trọng nhất: Xác định khách đã trả tiền thành công
  "data": {
    "object": {
      "id": "pi_3Mtw...",             // ID của Payment Intent
      "amount": 500000,
      "currency": "vnd",
      "status": "succeeded",
      "metadata": {
        "order_id": "1054"            //Đây chính là cái order_id đã nhét vào ở bước trước
      }
    }
  }
}
```

Lưu ý: Nếu giao dịch thất bại (khách hết tiền, thẻ bị khóa), Stripe sẽ gửi một event khác với type: "payment_intent.payment_failed".

**Response `200 OK`:**

- Stripe không quan tâm đến dữ liệu JSON trả về. Họ chỉ cần biết một điều duy nhất: "Server của anh đã nhận được thông báo chưa?"

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": {
    "received": true
  }
}
```

**Response `4xx`:**

```json 
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

HTTP Code,Mô tả
400 Bad Request,Lỗi xác thực chữ ký (Signature mismatch) - Báo hiệu có kẻ đang giả mạo Stripe.
500 Internal Server Error,Backend của đang bị lỗi (VD: không kết nối được Database để update trạng thái đơn). Stripe sẽ tự động gửi lại request này sau vài giờ.

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```


### 3.8. Xem lịch sử đặt bàn (danh sách dạng tóm tắt)
**`GET /api/v1/bookings/my-history`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xem lịch sử đặt bàn (tóm tắt) |
| **Auth**     | Bearer Token               |
| **Role**     | Customer                   |

**Query Parameters:**

| Param           | Type      | Required | Default | Mô tả           |
| --------------- | --------- | -------- | ------- | ---------------- |
| `page`          | `integer` | No       | `0`     | Số trang         |
| `limit`         | `integer` | No       | `10`    | Số phần tử/trang |
| `status`        | `string`  | No       | `null`  | Lọc theo trạng thái (AWAITING_CONFIRMATION, PENDING_PAYMENT, CONFIRMED, COMPLETED, CANCELLED, v.v.) |
| `fromDate`      | `string`  | No       | `null`  | Lọc theo thời gian `bookingStartTime` (ISO-8601) |
| `toDate`        | `string`  | No       | `null`  | Lọc theo thời gian `bookingStartTime` (ISO-8601) |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Lấy danh sách lịch sử đặt bàn",
  "data": [
    {
      "bookingId": 5001,
      "bookingTime": {
        "startTime": "2026-02-15T19:00:00",
        "endTime": "2026-02-15T21:00:00"
      },
      "quantity": 4,
      "status": "CONFIRMED",
      "depositAmount": 200000,
      "tables": [
        {
          "tableId": 1,
          "tableLabel": "Bàn 01"
        }
      ],
      "restaurant": {
        "restaurantId": 101,
        "restaurantName": "Haidilao - Chi nhánh Hùng Vương Plaza",
        "restaurantLogo": "https://abc.com/logo.png",
        "restaurantAddress": "126 Hùng Vương, Quận 5, TP.HCM"
      },
      "createdAt": "2026-02-10T14:30:00"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 15,
    "totalPages": 2
  }
}
```

**Response `4xx`:**

```json 
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

### 3.9. Xem đánh giá nhà hàng

**`GET /api/v1/restaurants/{restaurantId}/reviews`**

**Query Parameters:**

| Param    | Type      | Required | Default | Mô tả           |
| -------- | --------- | -------- | ------- | ---------------- |
| `limit`   | `integer` | No       | `10`    | Số phần tử/trang |
| `cursor`   | `integer` | No       | `null`    | ID của đánh giá cuối cùng |
| `rating`   | `integer` | No       | `null`    | Lọc theo đánh giá (1-5) |
| `sort`   | `string` | No       | `null`    | Sắp xếp (NEWEST, OLDEST) |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": [
    {
      "id": 9901,
      "userName": "Diệp Bảo Doanh",
      "userAvatar": "https://cdn.com/avatars/user_1.jpg",
      "rating": 5,
      "comment": "Không gian rất thoáng đãng, món sườn nướng cực kỳ ngon!",
      "createdAt": "2026-02-14T08:30:00Z"
    },
    {
      "id": 9902,
      "userName": "Hùng Nguyễn",
      "userAvatar": null,
      "rating": 4,
      "comment": "Nhân viên nhiệt tình nhưng chờ món hơi lâu một chút.",
      "createdAt": "2026-02-13T19:45:00Z"
    }
  ],
  "meta": {
    "nextCursor": 101, // ID của đánh giá cuối cùng
    "hasMore": true,
    "totalReviews": 1250
  }
}
```

**Response `4xx`:**

```json 
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

### 3.10. Đánh giá nhà hàng

**`POST /api/v1/restaurants/{restaurantId}/reviews`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Đánh giá nhà hàng            |
| **Auth**     | Bearer Token |
| **Role**     | Customer           |


**Path Parameters:**

| Param    | Type      | Required | Mô tả           |
| -------- | --------- | -------- | ---------------- |
| `restaurantId`   | `integer` | Yes      | ID của nhà hàng |

**Request Body:**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| `rating`     | `integer` (1-5)            |
| `comment`    | `string` (Tối đa 500 ký tự) |


**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Đánh giá thành công",
  "data": {
    "id": 9901,
    "userName": "Diệp Bảo Doanh",
    "userAvatar": "https://cdn.com/avatars/user_1.jpg",
    "rating": 5,
    "comment": "Không gian rất thoáng đãng, món sườn nướng cực kỳ ngon!",
    "createdAt": "2026-02-14T08:30:00Z"
  }
}
```

**Response `4xx`:**

```json 
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

### 3.11. Đặt bàn

**`POST /api/v1/restaurants/{id}/booking`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Đặt bàn tại nhà hàng dựa vào thời gian đặt, số lượng khách và danh sách bàn, ghi chú (nếu có), nhà hàng            |
| **Auth**     | Yes (Bearer Token) |
| **Role**     | Customer           |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `id`  | `integer` | Yes       | ID của nhà hàng |

**Request Body:**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| `restaurantId` | `Long` (ID của nhà hàng) |
| `bookingTime`     | `LocalDateTime` (Thời gian đặt)            |
| `quantity`    | `Long` (Số lượng khách) |
| `tableIds`    | `List<Long>` (Danh sách ID bàn) |
| `note`    | `String` (Ghi chú) |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Đặt bàn thành công",
  "data": {
    "bookingId": 101,
    "restaurantId": 1,
    "userId": 1,
    "bookingTime": "2026-02-10T14:30:00",
    "depositAmount": 100000,
    "quantity": 2,
    "tableIds": [1, 2],
    "bookingStatus": "PENDING_PAYMENT"
  }
}
```

**Response `4xx`:**

```json 
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```


---

### 3.12. Thanh toán đặt cọc

**`POST /api/v1/booking/{id}/payment`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Thanh toán đặt cọc            |
| **Auth**     | Yes (Bearer Token) |
| **Role**     | Customer           |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `id`  | `integer` | Yes       | ID của booking |

**Request Body:**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| `paymentMethod` | `String` (Phương thức thanh toán) |
| `amount`     | `Long` (Số tiền thanh toán)            |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thanh toán đặt cọc thành công",
  "data": {
    "bookingId": 101,
    "restaurantId": 1,
    "userId": 1,
    "bookingTime": "2026-02-10T14:30:00",
    "depositAmount": 100000,
    "quantity": 2,
    "tableIds": [1, 2],
    "bookingStatus": "PENDING_PAYMENT"
  }
}
```

**Response `4xx`:**

```json 
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

### 3.13. Đăng ký mở nhà hàng
**`POST /api/v1/restaurants/register`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Cho phép khách hàng đăng ký mở nhà hàng mới trên hệ thống. Yêu cầu sẽ được đưa vào trạng thái PENDING để Admin duyệt. |
| **Auth** | Yes (Bearer Token) |
| **Role** | CUSTOMER |

**Request Body (`multipart/form-data`):**
| Field | Type | Required | Mô tả |
| --- | --- | --- | --- |
| `name` | `string` | Yes | Tên nhà hàng |
| `description` | `string` | Yes | Mô tả nhà hàng |
| `address` | `string` | Yes | Địa chỉ |
| `latitude` | `double` | Yes | Vĩ độ |
| `longitude` | `double` | Yes | Kinh độ |
| `baseDepositValue` | `integer` | Yes | Mức cọc cơ bản |
| `depositPolicy` | `string` | Yes | "FIXED", "PER_GUEST", "NONE" |
| `cuisineIds` | `array[integer]` | Yes | Mảng ID danh mục ẩm thực (VD: 1, 2) |
| `logo` | `file` | Yes | File ảnh logo nhà hàng |
| `legalDocs` | `array[file]` | Yes | Danh sách file giấy tờ pháp lý (PDF, JPG) |

**Response `201 Created`:**
```json
{
  "status": 201,
  "message": "Đã gửi yêu cầu đăng ký nhà hàng thành công. Vui lòng chờ Admin duyệt.",
  "data": {
    "restaurantId": 105,
    "status": "PENDING"
  }
}
```

---

## 4. Module: Manager API

> Lưu ý: Tất cả các API trong module này đều yêu cầu `Auth: Bearer Token` và Role `ROLE_MANAGER` (hoặc manager có quyền với cơ sở hiện tại).

### 4.1. Thông tin nhà hàng

**`GET /api/v1/manager/restaurant`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy thông tin nhà hàng đang được quản lý bởi user hiện tại |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
    "restaurantId": 101,
    "name": "Haidilao - Chi nhánh Hùng Vương Plaza",
    "logo": "https://abc.com/logo.png",
    "description": "Thương hiệu lẩu nổi tiếng...",
    "address": "126 Hùng Vương, Quận 5, TP.HCM",
    "status": "OPENING",
    "baseDepositValue": 200000,
    "depositPolicy": "FIXED",
    "dayOfWeek": 7
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`PUT /api/v1/manager/restaurant`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật thông tin cơ bản của nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Request Body:**

```json
{
  "name": "string",
  "logo": "string",
  "description": "string",
  "address": "string",
  "baseDepositValue": "Long",
  "depositPolicy": "FIXED | PER_GUEST | NONE",
  "dayOfWeek": "Integer"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật thành công",
  "data": {
    "restaurantId": 101,
    "name": "Haidilao - Chi nhánh Hùng Vương Plaza",
    "logo": "https://abc.com/logo.png",
    "description": "Thương hiệu lẩu nổi tiếng...",
    "address": "126 Hùng Vương, Quận 5, TP.HCM",
    "status": "OPENING",
    "baseDepositValue": 200000,
    "depositPolicy": "FIXED",
    "dayOfWeek": 7
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`DELETE /api/v1/manager/restaurant`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa thành công",
  "data": null
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

### 4.2. Quản lý Nhân sự (Staff)

**`GET /api/v1/manager/staffs`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách nhân viên của nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Query Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `page`  | `integer` | No       | Trang |
| `size`  | `integer` | No       | Số lượng |
| `sort`  | `string` | No       | Sắp xếp |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "userId": 201,
      "fullName": "Nguyễn Văn B",
      "username": "waiter_b",
      "email": "b@example.com",
      "phone": "0981234567",
      "roles": [
        {
          "id": 2,
          "name": "ROLE_WAITER"
        }
      ],
      "status": "ACTIVE"
    }
  ],
  "meta": {
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`POST /api/v1/manager/staffs`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Thêm nhân viên mới vào nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Request Body:**

```json
{
  "username": "string, required",
  "password": "string, required",
  "fullName": "string, required",
  "email": "string, required",
  "phone": "string, required",
  "roleId": "Long, required"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Thêm nhân viên thành công",
  "data": {
      "userId": 201,
      "fullName": "Nguyễn Văn B",
      "username": "waiter_b",
      "email": "b@example.com",
      "phone": "0981234567",
      "roles": [
        {
          "id": 2,
          "name": "ROLE_WAITER"
        }
      ],
      "status": "ACTIVE"
    }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`PUT /api/v1/manager/staffs/{staffId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật thông tin nhân viên |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `staffId`  | `integer` | Yes       | ID của nhân viên |

**Request Body:**

```json
{
  "fullName": "string, required",
  "email": "string, required",
  "phone": "string, required",
  "status": "ACTIVE | BANNED, required",
  "roleId": "Long, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật nhân viên thành công",
  "data": {
      "userId": 201,
      "fullName": "Nguyễn Văn B",
      "username": "waiter_b",
      "email": "b@example.com",
      "phone": "0981234567",
      "roles": [
        {
          "id": 2,
          "name": "ROLE_WAITER"
        }
      ],
      "status": "ACTIVE"
    }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`DELETE /api/v1/manager/staffs/{staffId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa nhân viên khỏi nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `staffId`  | `integer` | Yes       | ID của nhân viên |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa nhân viên thành công",
  "data": null
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```


**`PATCH /api/v1/manager/staffs/{staffId}/kick`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Gỡ nhân viên khỏi nhà hàng (Set `workplace = null` và `role = CUSTOMER`). Lịch sử dữ liệu được giữ nguyên. |
| **Auth** | Yes (Bearer Token) |
| **Role** | MANAGER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã gỡ nhân viên khỏi nhà hàng thành công.",
  "data": null
}
```




---


### 4.3. Quản lý Menu

**`GET /api/v1/manager/menus`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách menu của nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "menuId": 1,
      "name": "Thực đơn chính",
      "description": "Dùng cho cả ngày"
    }
  ]
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`POST /api/v1/manager/menus`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Tạo Menu mới cho nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Tạo menu thành công",
  "data": {
    "menuId": 2,
    "name": "Thực đơn sáng",
    "description": "Dành cho bữa sáng"
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`PUT /api/v1/manager/menus/{menuId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật thông tin Menu |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `menuId`  | `integer` | Yes       | ID của Menu |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật menu thành công",
  "data": {
    "menuId": 1,
    "name": "Thực đơn chính",
    "description": "Dùng cho cả ngày"
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`DELETE /api/v1/manager/menus/{menuId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa Menu |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `menuId`  | `integer` | Yes       | ID của Menu |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa menu thành công",
  "data": null
}
```

---

**`GET /api/v1/manager/menus/{menuId}/food-groups`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách nhóm món ăn thuộc Menu |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `menuId`  | `integer` | Yes       | ID của Menu |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "groupId": 10,
      "name": "Món Chính",
      "description": "Các loại steak và salad"
    }
  ]
}
```

---

**`POST /api/v1/manager/menus/{menuId}/food-groups`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Thêm một nhóm món ăn vào Menu |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `menuId`  | `integer` | Yes       | ID của Menu |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Tạo nhóm món ăn thành công",
  "data": {
    "groupId": 10,
    "name": "Món Chính",
    "description": "Các loại món chính"
  }
}
```

---

**`PUT /api/v1/manager/food-groups/{foodGroupId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật thông tin nhóm món ăn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `foodGroupId`  | `integer` | Yes       | ID của FoodGroup |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật nhóm món ăn thành công",
  "data": {
    "groupId": 10,
    "name": "Món Chính",
    "description": "Các loại steak và salad"
  }
}
```

---

**`DELETE /api/v1/manager/food-groups/{foodGroupId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa nhóm món ăn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `foodGroupId`  | `integer` | Yes       | ID của FoodGroup |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa nhóm thành công",
  "data": null
}
```

---

**`GET /api/v1/manager/food-groups/{groupId}/foods`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách món ăn thuộc nhóm |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `groupId`  | `integer` | Yes       | ID của FoodGroup |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "foodId": 50,
      "name": "Bò lúc lắc",
      "description": "Bò Mỹ sốt tiêu",
      "image": "https://abc.com/bo-luc-lac.jpg",
      "price": 150000,
      "status": "OPENING",
      "optionGroups": [
        {
          "optionGroupId": 5,
          "name": "Chọn Cỡ",
          "status": "OPENING"
        }
      ]
    }
  ]
}
```

---

**`POST /api/v1/manager/food-groups/{foodGroupId}/foods`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Thêm một món ăn mới vào nhóm món ăn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `foodGroupId`  | `integer` | Yes       | ID của FoodGroup |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required",
  "image": "string, optional",
  "price": "Long, required",
  "status": "OPENING | CLOSED, required",
  "optionGroupIds": "[Long], optional"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Thêm món ăn thành công",
  "data": {
    "foodId": 50,
    "name": "Bò lúc lắc",
    "description": "Bò Mỹ sốt tiêu",
    "image": "https://abc.com/bo-luc-lac.jpg",
    "price": 150000,
    "status": "OPENING"
  }
}
```

---

**`PUT /api/v1/manager/foods/{foodId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật thông tin món ăn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `foodId`  | `integer` | Yes       | ID của món ăn (FoodDescription) |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required",
  "image": "string, optional",
  "price": "Long, required",
  "status": "OPENING | CLOSED, required",
  "optionGroupIds": "[Long], optional"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật món ăn thành công",
  "data": {
    "foodId": 50,
    "name": "Bò lúc lắc",
    "description": "Bò Mỹ sốt tiêu",
    "image": "https://abc.com/bo-luc-lac.jpg",
    "price": 150000,
    "status": "OPENING"
  }
}
```

---

**`DELETE /api/v1/manager/foods/{foodId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa món ăn khỏi menu |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `foodId`  | `integer` | Yes       | ID của món ăn |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa món ăn thành công",
  "data": null
}
```

---

**`GET /api/v1/manager/option-groups`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách tất cả nhóm tùy chọn đang có trong nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "optionGroupId": 5,
      "name": "Chọn Cỡ",
      "description": "Size của món ăn",
      "status": "OPENING"
    }
  ]
}
```

---

**`POST /api/v1/manager/option-groups`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Tạo nhóm tùy chọn mới (áp dụng chung, chưa móc vào món ăn) |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required",
  "status": "OPENING | CLOSED, required"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Tạo nhóm tùy chọn thành công",
  "data": {
    "optionGroupId": 5,
    "name": "Chọn Cỡ",
    "description": "Size của món ăn",
    "status": "OPENING"
  }
}
```

---

**`PUT /api/v1/manager/option-groups/{optionGroupId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật nhóm tùy chọn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `optionGroupId`  | `integer` | Yes       | ID của nhóm tùy chọn |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required",
  "status": "OPENING | CLOSED, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật thành công",
  "data": {
    "optionGroupId": 5,
    "name": "Chọn Cỡ",
    "description": "Size của món ăn",
    "status": "OPENING"
  }
}
```

---

**`DELETE /api/v1/manager/option-groups/{optionGroupId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa nhóm tùy chọn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `optionGroupId`  | `integer` | Yes       | ID của nhóm tùy chọn |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa thành công",
  "data": null
}
```

---

**`GET /api/v1/manager/option-groups/{groupId}/options`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách các tùy chọn chi tiết |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `optionGroupId`  | `integer` | Yes       | ID của nhóm tùy chọn |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "optionId": 21,
      "name": "Ít cay",
      "description": "Giảm bớt ớt",
      "price": 0,
      "status": "OPENING"
    }
  ]
}
```

---

**`POST /api/v1/manager/option-groups/{optionGroupId}/options`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Thêm một tùy chọn mới vào nhóm |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `optionGroupId`  | `integer` | Yes       | ID của nhóm tùy chọn |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required",
  "price": "Long, required",
  "status": "OPENING | CLOSED, required"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Thêm tùy chọn thành công",
  "data": {
    "optionId": 21,
    "name": "Ít cay",
    "description": "Giảm bớt ớt",
    "price": 0,
    "status": "OPENING"
  }
}
```

---

**`PUT /api/v1/manager/options/{optionId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật chi tiết tùy chọn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `optionId`  | `integer` | Yes       | ID của tùy chọn |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required",
  "price": "Long, required",
  "status": "OPENING | CLOSED, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật thành công",
  "data": {
    "optionId": 21,
    "name": "Ít cay",
    "description": "Giảm bớt ớt",
    "price": 0,
    "status": "OPENING"
  }
}
```

---

**`DELETE /api/v1/manager/options/{optionId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa tùy chọn khỏi nhóm |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `optionId`  | `integer` | Yes       | ID của tùy chọn |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa thành công",
  "data": null
}
```

---

### 4.4. Quản lý Sơ đồ bàn (Table Layout)

**`GET /api/v1/manager/table-areas`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách khu vực bàn ăn trong nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "tableAreaId": 1,
      "name": "Tầng 1",
      "status": "ACTIVE",
    }
  ]
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

**`POST /api/v1/manager/table-areas`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Thêm khu vực bàn ăn mới vào nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required",
  "status": "ACTIVE | INACTIVE, required"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Thêm khu vực bàn ăn thành công",
  "data": {
    "tableAreaId": 1,
    "name": "Tầng 1",
    "status": "ACTIVE"
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`PUT /api/v1/manager/table-areas/{tableAreaId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật thông tin khu vực bàn ăn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `tableAreaId`  | `integer` | Yes       | ID của khu vực bàn ăn |

**Request Body:**

```json
{
  "name": "string, required",
  "description": "string, required",
  "status": "ACTIVE | INACTIVE, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật khu vực bàn ăn thành công",
  "data": {
    "tableAreaId": 1,
    "name": "Tầng 1",
    "status": "ACTIVE"
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`DELETE /api/v1/manager/table-areas/{tableAreaId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa khu vực bàn ăn khỏi nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `tableAreaId`  | `integer` | Yes       | ID của khu vực bàn ăn |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa khu vực bàn ăn thành công",
  "data": {
    "tableAreaId": 1,
    "name": "Tầng 1",
    "status": "DELETED"
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`GET /api/v1/manager/table-areas/{tableAreaId}/tables`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách bàn ăn trong khu vực bàn ăn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `tableAreaId`  | `integer` | Yes       | ID của khu vực bàn ăn |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "tableId": 10,
      "name": "Bàn T1-01",
      "capacity": 4,
      "status": "AVAILABLE"
    }
  ]
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`POST /api/v1/manager/table-areas/{tableAreaId}/tables`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Thêm bàn ăn mới vào khu vực bàn ăn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `tableAreaId`  | `integer` | Yes       | ID của khu vực bàn ăn |

**Request Body:**

```json
{
  "name": "string, required",
  "capacity": "integer, required",
  "status": "AVAILABLE | OCCUPIED | RESERVED | CLEANING, required"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Thêm bàn ăn thành công",
  "data": {
    "tableId": 10,
    "name": "Bàn T1-01",
    "capacity": 4,
    "status": "AVAILABLE"
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`PUT /api/v1/manager/tables/{tableId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Cập nhật thông tin bàn ăn |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `tableId`  | `integer` | Yes       | ID của bàn ăn |

**Request Body:**

```json
{
  "name": "string, required",
  "capacity": "integer, required",
  "status": "AVAILABLE | OCCUPIED | RESERVED | CLEANING, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật bàn ăn thành công",
  "data": {
    "tableId": 10,
    "name": "Bàn T1-01",
    "capacity": 4,
    "status": "AVAILABLE"
  }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

---

**`DELETE /api/v1/manager/tables/{tableId}`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Xóa bàn ăn khỏi nhà hàng |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Manager                    |

**Path Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `tableId`  | `integer` | Yes       | ID của bàn ăn |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Xóa bàn ăn thành công",
  "data": null
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

### 4.5. Báo cáo doanh thu (Revenue Report)

**`GET /api/v1/manager/reports/overview`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy số liệu tổng quan (Dashboard Overview) dựa trên các giao dịch (Transaction) đã thanh toán thành công (CAPTURED). |
| **Auth** | Yes (Bearer Token) |
| **Role** | MANAGER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `fromDate` | `string` | No | `null` | Lọc từ ngày (ISO-8601) |
| `toDate` | `string` | No | `null` | Lọc đến ngày (ISO-8601) |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
    "totalRevenue": 25000000, 
    "netRevenue": 22500000, // Doanh thu sau khi đã trừ phí hoa hồng cho hệ thống
    "totalBookings": 120,
    "totalCustomers": 350,
    "averageBill": 208333
  }
}
```

**`GET /api/v1/manager/reports/revenue-chart`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy dữ liệu biểu đồ doanh thu theo thời gian |
| **Auth** | Yes (Bearer Token) |
| **Role** | MANAGER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `fromDate` | `string` | Yes | - | Lọc từ ngày (ISO-8601) |
| `toDate` | `string` | Yes | - | Lọc đến ngày (ISO-8601) |
| `timeUnit` | `string` | Yes | `DAY` | Nhóm theo `DAY`, `WEEK`, `MONTH` |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "date": "2026-02-10",
      "revenue": 5000000,
      "netRevenue": 4500000,
      "bookingsCount": 20
    },
    {
      "date": "2026-02-11",
      "revenue": 7000000,
      "netRevenue": 6300000,
      "bookingsCount": 35
    }
  ]
}
```

**`GET /api/v1/manager/reports/top-foods`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Báo cáo danh sách món ăn bán chạy nhất |
| **Auth** | Yes (Bearer Token) |
| **Role** | MANAGER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `fromDate` | `string` | No | `null` | Lọc từ ngày (ISO-8601) |
| `toDate` | `string` | No | `null` | Lọc đến ngày (ISO-8601) |
| `limit` | `integer` | No | `5` | Số lượng món cần lấy (Top 5, Top 10) |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "foodId": 12,
      "foodName": "Lẩu Tứ Xuyên",
      "quantitySold": 120,
      "revenue": 24000000
    },
    {
      "foodId": 5,
      "foodName": "Bò Wagyu",
      "quantitySold": 50,
      "revenue": 15000000
    }
  ]
}
```



---

## 5. Module: Notification

### 5.1. Xem danh sách thông báo
**`GET /api/v1/notifications`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Lấy danh sách thông báo của user đang đăng nhập (Cursor pagination) |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Mọi user đăng nhập                    |

**Query Parameters:**

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `cursor`  | `integer` | No       | ID của thông báo cuối cùng từ danh sách trước (Dùng để lấy next page) |
| `limit`  | `integer` | No       | Giới hạn số lượng (Default: 10) |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Lấy danh sách thông báo thành công",
  "data": [
    {
      "id": 1,
      "title": "Đặt bàn thành công",
      "content": "Bạn đã đặt bàn thành công...",
      "type": "BOOKING_CONFIRMED",
      "read": false,
      "metadata": {
         "bookingId": 123
      },
      "createdAt": "2026-04-20T07:45:00Z"
    }
  ],
  "meta": {
    "nextCursor": 5,
    "hasMore": true,
    "totalElements": 25
  }
}
```

---

### 5.2. Lấy số lượng thông báo chưa đọc
**`GET /api/v1/notifications/unread-count`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Đếm số lượng thông báo chưa đọc của user |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Mọi user đăng nhập                    |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Lấy số lượng thông báo chưa đọc thành công",
  "data": {
    "unreadCount": 5
  }
}
```

---

### 5.3. Đánh dấu 1 thông báo là đã đọc
**`PATCH /api/v1/notifications/{id}/read`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Đánh dấu một thông báo là đã đọc |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Mọi user đăng nhập                    |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Đã đánh dấu thông báo là đã đọc",
  "data": null
}
```

---

### 5.4. Đánh dấu tất cả thông báo là đã đọc
**`PATCH /api/v1/notifications/read-all`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Đánh dấu tất cả thông báo thành đã đọc |
| **Auth**     | Yes (Bearer Token)       |
| **Role**     | Mọi user đăng nhập                    |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Đã đánh dấu tất cả thông báo là đã đọc",
  "data": null
}
```

---

## 6. Module: Receptionist Booking

### 6.1. Xác nhận yêu cầu đặt bàn
**`PATCH /api/v1/receptionist/bookings/{bookingId}/confirm`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Cập nhật trạng thái đặt bàn tương ứng với DepositPolicy (`CONFIRMED` hoặc `PENDING_PAYMENT`), trigger gửi thông báo cho Customer, và lên lịch kiểm tra thanh toán nếu cần. |
| **Auth** | Yes (Bearer Token) |
| **Role** | RECEPTIONIST / MANAGER |

**Response `200 OK` (Trường hợp Có cần cọc):**
```json
{
  "status": 200,
  "message": "Đã xác nhận yêu cầu đặt bàn, chờ khách hàng thanh toán cọc trong 15 phút.",
  "data": {
    "bookingId": 1234,
    "status": "PENDING_PAYMENT",
    "depositAmount": 500000
  }
}
```

**Response `200 OK` (Trường hợp KHÔNG cần cọc):**
```json
{
  "status": 200,
  "message": "Đã xác nhận yêu cầu đặt bàn (Không yêu cầu cọc).",
  "data": {
    "bookingId": 1235,
    "status": "CONFIRMED",
    "depositAmount": 0
  }
}
```

---

### 6.2. Từ chối yêu cầu đặt bàn
**`PATCH /api/v1/receptionist/bookings/{bookingId}/reject`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Cập nhật trạng thái đặt bàn thành REJECTED, trigger gửi thông báo lý do cho Customer |
| **Auth** | Yes (Bearer Token) |
| **Role** | RECEPTIONIST / MANAGER |

**Request Body:**
```json
{
  "reason": "Nhà hàng đã hết bàn ở khu vực VIP lúc 19:00, mong quý khách thông cảm."
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã từ chối đặt bàn.",
  "data": {
    "bookingId": 1234,
    "status": "REJECTED"
  }
}
```

### 6.3. Lễ tân Check-in bàn đã đặt
**`PATCH /api/v1/receptionist/bookings/{bookingId}/check-in`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | (Bước 1) Xác nhận khách đã đến. Chuyển trạng thái Table sang OCCUPIED, khởi tạo TableSession (ACTIVE, waiter_id: null). |
| **Auth** | Yes (Bearer Token) |
| **Role** | RECEPTIONIST, MANAGER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Check-in thành công",
  "data": {
    "sessionId": 1001,
    "tableIds": [1, 2],
    "status": "ACTIVE"
  }
}
```


### 6.4 Lấy danh sách Booking (lọc theo trạng thái)
**`GET /api/v1/receptionist/bookings`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy ra danh sách booking theo trạng thái (phân trang, sắp xếp). |
| **Auth** | Yes (Bearer Token) |
| **Role** | RECEPTIONIST / MANAGER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `page` | `integer` | No | `0` | Số trang |
| `limit` | `integer` | No | `10` | Số phần tử/trang |
| `status` | `string` | No | `CONFIRMED` | Mặc định là CONFIRMED (có thể lọc trạng thái khác nếu muốn, ví dụ: AWAITING_CONFIRMATION) |
| `sort` | `string` | No | `bookingTime.startTime,asc` | Sắp xếp để xem khách nào đến sớm nhất |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Lấy danh sách booking thành công",
  "data": [
    {
      "bookingId": 1234,
      "customerName": "Nguyễn Văn A",
      "customerPhone": "0987654321",
      "bookingTime": "2026-02-15T19:00:00",
      "numberOfPeople": 4,
      "status": "CONFIRMED"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 5,
    "totalPages": 1
  }
}
```

### 6.5 Lấy danh sách Booking chờ xác nhận
**`GET /api/v1/receptionist/bookings/awaiting-confirmation`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy ra danh sách booking đang chờ xác nhận (status = AWAITING_CONFIRMATION). |
| **Auth** | Yes (Bearer Token) |
| **Role** | RECEPTIONIST / MANAGER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `page` | `integer` | No | `0` | Số trang |
| `limit` | `integer` | No | `10` | Số phần tử/trang |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Lấy danh sách yêu cầu đặt bàn chờ xác nhận thành công",
  "data": [
    {
      "bookingId": 1235,
      "customerName": "Trần Thị B",
      "customerPhone": "0123456789",
      "bookingTime": "2026-02-15T20:00:00",
      "numberOfPeople": 2,
      "status": "AWAITING_CONFIRMATION"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 2,
    "totalPages": 1
  }
}
```

### 6.5. Xem chi tiết Booking đã Confirmed
**`GET /api/v1/receptionist/bookings/{bookingId}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Xem thông tin chi tiết của Booking (để đối chiếu thông tin với khách). |
| **Auth** | Yes (Bearer Token) |
| **Role** | RECEPTIONIST / MANAGER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
    "bookingId": 1234,
    "customer": {
      "userId": 5,
      "fullName": "Nguyễn Văn A",
      "phone": "0987654321"
    },
    "bookingTime": "2026-02-15T19:00:00",
    "numberOfPeople": 4,
    "note": "Khách thích ngồi cạnh cửa sổ",
    "depositAmount": 500000,
    "status": "CONFIRMED",
    "assignedTables": [
      {
        "tableId": 1,
        "label": "Bàn VIP 01"
      }
    ]
  }
}
```

### 6.6. Hủy Booking do khách không đến
**`PATCH /api/v1/receptionist/bookings/{bookingId}/cancel`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Hủy bỏ các Booking khi đã quá thời gian dự kiến nhưng khách vẫn chưa đến để chừa bàn cho khách khác. |
| **Auth** | Yes (Bearer Token) |
| **Role** | RECEPTIONIST / MANAGER |

**Request Body:**
```json
{
  "reason": "Khách quá giờ 30 phút không đến và không liên lạc được"
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã hủy booking thành công",
  "data": {
    "bookingId": 1234,
    "status": "CANCELLED"
  }
}
```

---

## 7. Module: Admin API

### 7.1. Duyệt nhà hàng mới
**`PATCH /api/v1/admin/restaurants/{id}/approval`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Duyệt hoặc từ chối yêu cầu đăng ký mở nhà hàng |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Path Parameters:**
| Param | Type | Required | Mô tả |
| --- | --- | --- | --- |
| `id` | `integer` | Yes | ID của nhà hàng |

**Request Body:**
```json
{
  "status": "APPROVED", // hoặc "REJECTED"
  "rejectReason": "Thiếu giấy phép kinh doanh hợp lệ" // Bắt buộc nếu status = REJECTED
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã phê duyệt nhà hàng thành công.",
  "data": {
    "restaurantId": 105,
    "status": "APPROVED"
  }
}
```

### 7.2. Lấy danh sách nhà hàng (CRUD)
**`GET /api/v1/admin/restaurants`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy danh sách nhà hàng với các bộ lọc dành cho Admin |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `page` | `integer` | No | `0` | Số trang |
| `limit` | `integer` | No | `10` | Số phần tử/trang |
| `status` | `string` | No | `null` | Lọc theo trạng thái (PENDING, APPROVED, REJECTED, SUSPENDED...) |
| `search` | `string` | No | `null` | Tìm kiếm theo tên hoặc địa chỉ nhà hàng |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Lấy danh sách nhà hàng thành công",
  "data": [
    {
      "restaurantId": 105,
      "restaurantName": "Haidilao",
      "managerName": "Nguyễn Văn A",
      "status": "PENDING",
      "createdAt": "2026-03-01T10:00:00"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 50,
    "totalPages": 5
  }
}
```

### 7.3. Xem chi tiết nhà hàng
**`GET /api/v1/admin/restaurants/{id}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy thông tin chi tiết nhà hàng, bao gồm cả manager và các giấy tờ pháp lý |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
    "restaurantId": 105,
    "restaurantName": "Haidilao",
    "status": "PENDING",
    "manager": {
      "userId": 10,
      "fullName": "Nguyễn Văn A",
      "phone": "0987654321"
    },
    "legalDocs": [
      {
        "docId": 1,
        "docUrl": "https://abc.com/giay-phep.pdf"
      }
    ]
  }
}
```

### 7.4. Khóa / Mở khóa nhà hàng
**`PATCH /api/v1/admin/restaurants/{id}/status`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Thay đổi trạng thái hoạt động của nhà hàng (VD: Đình chỉ) |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**
```json
{
  "status": "SUSPENDED",
  "reason": "Vi phạm chính sách nền tảng"
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Cập nhật trạng thái thành công",
  "data": {
    "restaurantId": 105,
    "status": "SUSPENDED"
  }
}
```

### 7.5. Quản lý danh mục Cuisine
**`GET /api/v1/admin/cuisines`** (Dùng chung endpoint lấy list `GET /api/v1/cuisines` nếu đã public, hoặc tự định nghĩa 1 cái riêng, nhưng thường list là public)

**`POST /api/v1/admin/cuisines`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Thêm mới một danh mục ẩm thực |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**
```json
{
  "name": "Món Ý"
}
```

**Response `201 Created`:**
```json
{
  "status": 201,
  "message": "Tạo danh mục thành công",
  "data": {
    "cuisineId": 5,
    "name": "Món Ý"
  }
}
```

**`PUT /api/v1/admin/cuisines/{id}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Cập nhật tên danh mục ẩm thực |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**
```json
{
  "name": "Món Ý (Italian)"
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Cập nhật thành công",
  "data": {
    "cuisineId": 5,
    "name": "Món Ý (Italian)"
  }
}
```

**`DELETE /api/v1/admin/cuisines/{id}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Xóa danh mục ẩm thực |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Xóa thành công",
  "data": null
}
```

### 7.6. Cấu hình Commission (Hoa hồng)
**`PATCH /api/v1/admin/restaurants/{id}/commission`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Cấu hình mức hoa hồng riêng cho một nhà hàng cụ thể |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**
```json
{
  "commissionType": "PERCENTAGE", // "PERCENTAGE" hoặc "FIXED"
  "baseCommissionValue": 10 // Nghĩa là 10% nếu type là PERCENTAGE
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Cấu hình hoa hồng thành công",
  "data": {
    "restaurantId": 105,
    "commissionType": "PERCENTAGE",
    "baseCommissionValue": 10
  }
}
```

### 7.7. Báo cáo toàn hệ thống
**`GET /api/v1/admin/reports/dashboard`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy số liệu thống kê tổng quan của toàn hệ thống |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `fromDate` | `string` | No | `null` | Lọc từ ngày (ISO-8601) |
| `toDate` | `string` | No | `null` | Lọc đến ngày (ISO-8601) |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Lấy dữ liệu dashboard thành công",
  "data": {
    "totalRevenue": 15000000, // Doanh thu hoa hồng
    "totalBookings": 1250,
    "totalRestaurants": 45,
    "totalNewUsers": 120
  }
}
```

### 7.8. Gửi thông báo (Broadcast)
**`POST /api/v1/admin/notifications/broadcast`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Gửi thông báo đến tất cả người dùng hoặc theo Role |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**
```json
{
  "title": "Bảo trì hệ thống",
  "content": "Hệ thống sẽ bảo trì từ 00:00 đến 02:00 ngày mai.",
  "type": "SYSTEM_MESSAGE"
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã đưa vào hàng đợi gửi thông báo",
  "data": null
}
```

### 7.9. Gửi thông báo cho cá nhân
**`POST /api/v1/admin/notifications/send`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Gửi thông báo cho một người dùng cụ thể |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**
```json
{
  "userId": 105,
  "title": "Nhắc nhở cập nhật thông tin",
  "content": "Vui lòng cập nhật đầy đủ giấy phép kinh doanh.",
  "type": "SYSTEM_MESSAGE"
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã gửi thông báo thành công",
  "data": null
}
```



### 7.10. Quản lý Người dùng (User Management)

Admin có toàn quyền quản trị tài khoản và điều phối nhân sự trên toàn bộ hệ thống.

#### 7.10.1. Lấy danh sách toàn bộ người dùng
**`GET /api/v1/admin/users`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Danh sách tài khoản hệ thống |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `page` | `integer` | No | `0` | Số trang |
| `limit` | `integer` | No | `10` | Số phần tử/trang |
| `role` | `string` | No | `null` | Lọc theo Role |
| `restaurantId` | `integer` | No | `null` | Lọc nhân sự theo nhà hàng |
| `status` | `string` | No | `null` | BANNED / ACTIVE |
| `search` | `string` | No | `null` | Tìm kiếm theo tên/username/email |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "userId": 201,
      "fullName": "Nguyễn Văn B",
      "username": "waiter_b",
      "email": "b@example.com",
      "phone": "0981234567",
      "roles": [
        {
          "id": 2,
          "name": "ROLE_WAITER"
        }
      ],
      "status": "ACTIVE"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 50,
    "totalPages": 5
  }
}
```

#### 7.10.2. Lấy thông tin chi tiết một người dùng
**`GET /api/v1/admin/users/{userId}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy thông tin chi tiết của một tài khoản |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
      "userId": 201,
      "fullName": "Nguyễn Văn B",
      "username": "waiter_b",
      "email": "b@example.com",
      "phone": "0981234567",
      "roles": [
        {
          "id": 2,
          "name": "ROLE_WAITER"
        }
      ],
      "status": "ACTIVE",
      "address": "Hà Nội",
      "avatar": "https://example.com/avatar.jpg",
      "workplace": { //Nếu có workplace thì sẽ trả về object này, nếu không thì null
        "restaurantId": 1,
        "name": "Nhà hàng A",
        "status": "ACTIVE",
        "avatar": "https://example.com/avatar.jpg"
      }
    }
}
```

#### 7.10.3. Tạo tài khoản (Admin)
**`POST /api/v1/admin/users`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Tạo tài khoản bất kỳ (Có thể chọn Role thoải mái) |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**

```json
{
  "username": "string, required",
  "password": "string, required",
  "fullName": "string, required",
  "email": "Nếu không có thì backend tự tạo email dummy trước",
  "phone": "string, required",
  "roleId": "Long, required"
}
```

**Response `201 Created`:**

```json
{
  "status": 201,
  "message": "Thêm nhân viên thành công",
  "data": {
      "userId": 201,
      "fullName": "Nguyễn Văn B",
      "username": "waiter_b",
      "email": "b@example.com",
      "phone": "0981234567",
      "roles": [
        {
          "id": 2,
          "name": "ROLE_WAITER"
        }
      ],
      "status": "ACTIVE"
    }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```

#### 7.10.4. Cập nhật thông tin cá nhân
**`PUT /api/v1/admin/users/{userId}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Sửa thông tin tài khoản |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**

```json
{
  "fullName": "string, required",
  "email": "string, required",
  "phone": "string, required",
  "status": "ACTIVE | BANNED, required",
  "roleId": "Long, required"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Cập nhật nhân viên thành công",
  "data": {
      "userId": 201,
      "fullName": "Nguyễn Văn B",
      "username": "waiter_b",
      "email": "b@example.com",
      "phone": "0981234567",
      "roles": [
        {
          "id": 2,
          "name": "ROLE_WAITER"
        }
      ],
      "status": "ACTIVE"
    }
}
```

**Response `4xx`:**

```json
{
  "status": 400,
  "message": "Mô tả lỗi",
  "errors": { }
}
```



#### 7.10.7. Gán / Đổi Quản lý (Manager) cho Nhà hàng
**`PATCH /api/v1/admin/restaurants/{restaurantId}/manager`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Gán một tài khoản làm Manager chính thức cho nhà hàng. |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Logic xử lý backend:**
1. Thăng cấp User lên `MANAGER`.
2. Cập nhật `workplace_restaurant_id = {restaurantId}` cho User.
3. Cập nhật `manager_id = {userId}` cho Restaurant.

**Request Body:**
```json
{
  "userId": 123
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã bổ nhiệm Manager cho nhà hàng thành công.",
  "data": null
}
```

#### 7.10.8. Gán / Gỡ Nhân sự (Staff) cho Nhà hàng
**`PATCH /api/v1/admin/users/{userId}/workplace`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Điều chuyển nhân viên vào làm việc tại một nhà hàng, hoặc gỡ nhân viên khỏi nhà hàng. |
| **Auth** | Yes (Bearer Token) |
| **Role** | ADMIN |

**Request Body:**
```json
{
  "restaurantId": 456,
  "roleId": 2
}
```

> **Lưu ý Gỡ nhân viên:** Truyền `restaurantId = null` và `roleId = 3` nếu muốn rút nhân viên khỏi nhà hàng.

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã điều chuyển nhân sự thành công.",
  "data": null
}
```

---

## 8. State Machine (Vòng đời thực thể)

Để quy trình vận hành mượt mà, hệ thống tuân thủ các bộ trạng thái sau:

- **Table (Bàn)**: 
  `AVAILABLE` (Trống) ➔ `OCCUPIED` (Có khách) ➔ `MAINTENANCE` (Bảo trì)
- **RestaurantTableSession (Phiên bàn)**: 
  `ACTIVE` (Mới mở, Lễ tân thao tác) ➔ `SERVING` (Đang phục vụ, Phục vụ đảm nhận) ➔ `SERVED` (Phục vụ hoàn tất) ➔ `PAYING` (Đang thanh toán, Khóa order) ➔ `COMPLETED` (Hoàn tất, Bàn trống).
- **FoodOrder (Đơn gọi món)**: 
  `TAKING_ORDER` (Đang ghi món nháp) ➔ `CONFIRMED` (Đã chốt món gửi bếp) ➔ `COMPLETED` (Đã bưng xong hết) hoặc `CANCELLED` (Hủy).
- **FoodItem (Món ăn chi tiết)**: 
  `PENDING` (Đang chờ chế biến) ➔ `SERVED` (Đã phục vụ) hoặc `CANCELLED` (Hết món/Hủy).

---

## 9. Module: Waiter API

### 9.1. Đảm nhận phiên bàn
**`PATCH /api/v1/waiter/sessions/{sessionId}/assign`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | (Bước 2) Phục vụ nhận bàn. Cập nhật `waiter_id` và chuyển TableSession sang `SERVING`. Chỉ Phục vụ này mới có quyền tạo Order. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Nhận bàn thành công",
  "data": {
    "sessionId": 1001,
    "status": "SERVING",
    "waiterId": 15
  }
}
```

### 9.2. Tạo FoodOrder mới (Khởi tạo ghi món)
**`POST /api/v1/waiter/sessions/{sessionId}/orders`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | (Bước 3.1) Tạo một Order rỗng với trạng thái `TAKING_ORDER` để chuẩn bị ghi món trên thiết bị của phục vụ. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Response `201 Created`:**
```json
{
  "status": 201,
  "message": "Tạo Order thành công",
  "data": {
    "orderId": 5001,
    "status": "TAKING_ORDER"
  }
}
```

### 9.3. Xác nhận và Gửi bếp
**`POST /api/v1/waiter/orders/{orderId}/confirm-items`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | (Bước 3.2) Gửi toàn bộ danh sách món ăn từ Client lên Server. Lưu vào DB, chuyển FoodOrder thành `CONFIRMED` và các FoodItem thành `PENDING`. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Request Body:**
```json
{
  "items": [
    {
      "foodDescriptionId": 12,
      "quantity": 2,
      "optionIds": [1, 5] // Các tuỳ chọn đi kèm (VD: Size L, Ít đá)
    },
    {
      "foodDescriptionId": 15,
      "quantity": 1,
      "optionIds": []
    }
  ]
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã gửi bếp thành công",
  "data": {
    "orderId": 5001,
    "status": "CONFIRMED",
    "itemsCount": 2
  }
}
```

### 9.4. Xử lý trạng thái món ăn (Bưng ra / Hủy món)
**`PATCH /api/v1/waiter/food-items/{itemId}/status`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | (Bước 4) Phục vụ cập nhật món đã bưng lên (`SERVED`) hoặc Hủy do bếp hết đồ (`CANCELLED`). Giữ lại bản ghi để lưu vết. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Request Body:**
```json
{
  "status": "SERVED" // hoặc "CANCELLED"
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Cập nhật món ăn thành công",
  "data": {
    "itemId": 8001,
    "status": "SERVED"
  }
}
```

### 9.5. Hoàn tất FoodOrder
**`PATCH /api/v1/waiter/orders/{orderId}/complete`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | (Bước 4) Khi tất cả món ăn đã `SERVED` hoặc `CANCELLED`, Phục vụ chốt Order chuyển sang `COMPLETED`. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã hoàn tất FoodOrder",
  "data": {
    "orderId": 5001,
    "status": "COMPLETED"
  }
}
```

### 9.6. Lấy danh sách phiên bàn chờ phục vụ
**`GET /api/v1/waiter/sessions`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy ra danh sách phiên bàn hiện đang có và chưa ai đảm nhận. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `page` | `integer` | No | `0` | Số trang |
| `limit` | `integer` | No | `10` | Số phần tử/trang |
| `status` | `string` | No | `ACTIVE` | Lọc theo trạng thái phiên bàn |
| `unassigned` | `boolean` | No | `true` | Lọc những bàn chưa có phục vụ nào đảm nhận |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Lấy danh sách phiên bàn thành công",
  "data": [
    {
      "sessionId": 1001,
      "tableLabels": ["Bàn 01", "Bàn 02"],
      "customerName": "Nguyễn Văn A",
      "numberOfPeople": 4,
      "status": "ACTIVE",
      "createdAt": "2026-04-20T19:05:00"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 5,
    "totalPages": 1
  }
}
```

### 9.7. Lấy danh sách phiên bàn đang phục vụ
**`GET /api/v1/waiter/sessions/me`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy ra danh sách các phiên bàn đang được đảm nhận bởi nhân viên phục vụ hiện tại (status = SERVING, waiter_id = currentUserId). |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `page` | `integer` | No | `0` | Số trang |
| `limit` | `integer` | No | `10` | Số phần tử/trang |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Lấy danh sách bàn đang phục vụ thành công",
  "data": [
    {
      "sessionId": 1001,
      "tableLabels": ["Bàn 01", "Bàn 02"],
      "customerName": "Nguyễn Văn A",
      "numberOfPeople": 4,
      "status": "SERVING",
      "createdAt": "2026-04-20T19:05:00"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 5,
    "totalPages": 1
  }
}
```

### 9.8. Xem chi tiết phiên bàn
**`GET /api/v1/waiter/sessions/{sessionId}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Xem thông tin chi tiết của TableSession bao gồm bàn, số lượng người, và danh sách FoodOrders tóm tắt. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
    "sessionId": 1001,
    "tables": [
      { "tableId": 1, "label": "Bàn 01" }
    ],
    "numberOfPeople": 4,
    "status": "SERVING",
    "foodOrders": [
      {
        "orderId": 5001,
        "status": "CONFIRMED",
        "itemsCount": 3,
        "createdAt": "2026-04-20T19:10:00"
      }
    ]
  }
}
```

### 9.8. Lấy thực đơn nhà hàng (Dành cho ghi món)
**`GET /api/v1/waiter/menu`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy toàn bộ thực đơn kèm chi tiết OptionGroups và Options để phục vụ chọn món trên App. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Lấy menu thành công",
  "data": {
    "menuId": 1,
    "menuName": "Thực Đơn Mùa Hè",
    "foodGroups": [
      {
        "groupId": 1,
        "name": "Món Chính",
        "foods": [
          {
            "foodDescriptionId": 12,
            "name": "Bò Wagyu Nướng",
            "price": 500000,
            "optionGroups": [
              {
                "optionGroupId": 5,
                "name": "Độ chín",
                "options": [
                  { "optionId": 21, "name": "Medium Rare", "price": 0 },
                  { "optionId": 22, "name": "Well Done", "price": 0 }
                ]
              }
            ]
          }
        ]
      }
    ]
  }
}
```

### 9.9. Xem chi tiết FoodOrder
**`GET /api/v1/waiter/orders/{orderId}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Xem chi tiết các món trong một FoodOrder để kiểm tra / bưng bê. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
    "orderId": 5001,
    "status": "CONFIRMED",
    "items": [
      {
        "itemId": 8001,
        "foodName": "Bò Wagyu Nướng",
        "quantity": 1,
        "selectedOptions": ["Medium Rare"],
        "status": "PENDING"
      }
    ]
  }
}
```

### 9.10. Hủy FoodOrder
**`PATCH /api/v1/waiter/orders/{orderId}/cancel`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Hủy bỏ FoodOrder đã xác nhận và gửi bếp. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Request Body:**
```json
{
  "reason": "Khách muốn đổi món khác hoàn toàn"
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã hủy order thành công",
  "data": {
    "orderId": 5001,
    "status": "CANCELLED"
  }
}
```

### 9.11. Xác nhận hoàn tất phiên phục vụ
**`PATCH /api/v1/waiter/sessions/{sessionId}/serve-complete`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Chuyển trạng thái TableSession sang `SERVED` và Booking sang `SERVED` để Thu ngân có thể bắt đầu tính tiền. |
| **Auth** | Yes (Bearer Token) |
| **Role** | WAITER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Hoàn tất phục vụ, chờ thanh toán",
  "data": {
    "sessionId": 1001,
    "status": "SERVED"
  }
}
```

---
## 10. Module: Cashier API (Thu ngân)

### 10.1. Lấy danh sách phiên bàn chờ thanh toán
**`GET /api/v1/cashier/sessions`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy ra danh sách các TableSession đã phục vụ xong (SERVED) để Thu ngân chuẩn bị thanh toán. |
| **Auth** | Yes (Bearer Token) |
| **Role** | CASHIER, MANAGER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `page` | `integer` | No | `0` | Số trang |
| `limit` | `integer` | No | `10` | Số phần tử/trang |
| `status` | `string` | No | `SERVED` | Lọc theo trạng thái phiên bàn |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "sessionId": 1001,
      "tableLabels": ["Bàn 01"],
      "customerName": "Nguyễn Văn A",
      "numberOfPeople": 4,
      "status": "SERVED"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 1,
    "totalPages": 1
  }
}
```

### 10.2. Lấy danh sách phiên bàn đang chờ thanh toán
**`GET /api/v1/cashier/sessions/paying`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy ra danh sách các TableSession đã khởi tạo thanh toán (đang ở trạng thái PAYING). |
| **Auth** | Yes (Bearer Token) |
| **Role** | CASHIER, MANAGER |

**Query Parameters:**
| Param | Type | Required | Default | Mô tả |
| --- | --- | --- | --- | --- |
| `page` | `integer` | No | `0` | Số trang |
| `limit` | `integer` | No | `10` | Số phần tử/trang |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Lấy danh sách phiên bàn đang thanh toán thành công",
  "data": [
    {
      "sessionId": 1001,
      "tableLabels": ["Bàn 01"],
      "customerName": "Nguyễn Văn A",
      "numberOfPeople": 4,
      "status": "PAYING"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "totalItems": 1,
    "totalPages": 1
  }
}
```

### 10.3. Xem chi tiết phiên bàn để thanh toán
**`GET /api/v1/cashier/sessions/{sessionId}`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | Lấy chi tiết toàn bộ các món ăn, giá tiền, tổng tiền và tiền cọc của Session. |
| **Auth** | Yes (Bearer Token) |
| **Role** | CASHIER, MANAGER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
    "sessionId": 1001,
    "tableLabels": ["Bàn 01"],
    "customerName": "Nguyễn Văn A",
    "numberOfPeople": 4,
    "depositAmount": 500000,
    "totalAmount": 1500000,
    "status": "SERVED",
    "items": [
      {
        "foodName": "Bò Wagyu Nướng",
        "quantity": 2,
        "price": 500000,
        "totalItemPrice": 1000000
      },
      {
        "foodName": "Lẩu Thái",
        "quantity": 1,
        "price": 500000,
        "totalItemPrice": 500000
      }
    ]
  }
}
```

### 10.3. Khởi tạo thanh toán
**`PATCH /api/v1/cashier/sessions/{sessionId}/initiate-payment`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | (Bước 5) Khách yêu cầu tính tiền. Kiểm tra không có Order nào `PENDING`. Chuyển Session sang `PAYING`. Khóa quyền thêm order của Phục vụ. |
| **Auth** | Yes (Bearer Token) |
| **Role** | CASHIER, MANAGER |

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Đã chuyển sang trạng thái chờ thanh toán",
  "data": {
    "sessionId": 1001,
    "status": "PAYING"
  }
}
```

### 10.4. Hoàn tất hóa đơn (Thanh toán xong)
**`POST /api/v1/cashier/sessions/{sessionId}/complete`**

| Thuộc tính | Giá trị |
| --- | --- |
| **Summary** | (Bước 6) Thu ngân thu tiền. Chuyển TableSession sang `COMPLETED` và giải phóng Table về `AVAILABLE`. Thanh toán tại quán dùng Tiền mặt (CASH). |
| **Auth** | Yes (Bearer Token) |
| **Role** | CASHIER, MANAGER |

**Request Body:**
```json
{
  "paymentMethod": "CASH",
  "totalAmount": 1000000 
}
```

**Response `200 OK`:**
```json
{
  "status": 200,
  "message": "Thanh toán hoàn tất, bàn đã trống.",
  "data": {
    "sessionId": 1001,
    "status": "COMPLETED"
  }
}
```
