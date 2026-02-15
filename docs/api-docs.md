# API Contract - Hệ thống Đặt bàn Nhà hàng

> **Base URL:** `http://localhost:8080/api/v1`
> **Version:** 1.0.0
> **Last Updated:** 2026-02-15

---

## Mục lục

- [1. Quy ước chung](#1-quy-ước-chung)
- [2. Authentication](#2-authentication)
- [3. Module: Quản lý nhà hàng](#3-module-quản-lý-nhà-hàng)

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
      "id": 1,
      "username": "admin",
      "fullName": "Nguyễn Văn A",
      "role": "ADMIN"
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


---


### 1. Tìm kiếm nhà hàng 

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
| `address`   | `string` | Yes       | `null`    | Địa chỉ do khách hàng đưa ra |
| `cuisine`   | `string` | No       | `null`    | Loại hình ẩm thực |
| `radius`   | `integer` | No       | `2`    | Bán kính tìm kiếm |


**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": [ //Dữ liệu chính hoặc Array
    {
    "id": 101,
    "name": "Haidilao - Chi nhánh Hùng Vương Plaza",
    "logo": "https://abc.com/logo.png",
    "cuisines": ["Lẩu", "Món Trung"],
    "avgRating": 4.8,
    "totalReviews": 1250,
    "address": "126 Hùng Vương, Quận 5, TP.HCM",
    "distance": 1.2, // km (Được tính từ tọa độ của khách hàng so với tọa độ của nhà hàng)
    "depositType": "Fixed",
    "baseDeposit": 200000,
    "isOpen": true, // (Tính toán giữa thời gian thực tế và thời gian mở cửa của nhà hàng)
    "direction": {} // (Thông tin về chỉ đường và khoảng cách)
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

### 2. Lấy chi tiết nhà hàng

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

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": {
    "id": 101,
    "name": "Haidilao - Chi nhánh Hùng Vương Plaza",
    "description": "Thương hiệu lẩu nổi tiếng với dịch vụ chăm sóc khách hàng tận tâm...",
    "address": "126 Hùng Vương, Quận 5, TP.HCM",
    "latitude": 10.758,
    "longitude": 106.663,
    "cuisines": ["Lẩu", "Món Trung"],
    "avgRating": 4.8,
    "totalReviews": 1250,
    "depositPolicy": "Fixed",
    "baseDepositAmount": 200000,
    "operationTimes": [
      { "day": "Monday", "open": "09:00", "close": "23:00" },
      { "day": "Tuesday", "open": "09:00", "close": "23:00" }
    ],
    "menus": [
      {
        "categoryName": "Món lẩu",
        "foods": [
          {
            "id": 501,
            "name": "Lẩu Thái Hai ngăn",
            "price": 350000,
            "description": "Vị chua cay đặc trưng...",
            "optionsGroup": [
              {
                "id": 1,
                "name": "Độ cay",
                "options": [
                  {
                    "id": 1,
                    "name": "Ít cay",
                    "price": 0,
                    "image": "https://abc.com/logo.png"
                  },
                  {
                    "id": 2,
                    "name": "Cay vừa",
                    "price": 0,
                    "image": "https://abc.com/logo.png"
                  },
                  {
                    "id": 3,
                    "name": "Cay nhiều",
                    "price": 0,
                    "image": "https://abc.com/logo.png"
                  }
                ]
              }
            ]
          }
        ],
      }
    ],
    "tableAreas": [
      { "areaName": "Trong nhà", "availableTables": 15, "maxCapacity": 10 },
      { "areaName": "VIP", "availableTables": 2, "maxCapacity": 20 }
    ],
    "topReviews": [
      {
        "userName": "Doanh Diệp",
        "rating": 5,
        "comment": "Dịch vụ rất tốt, món ăn ngon!",
        "createdAt": "2026-02-10"
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

---

### 3. Xem chi tiết danh sách bàn cho việc đặt bàn
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
### 4. Tạo thanh toán qua Stripe
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

### 5. Xác nhận thanh toán qua Stripe
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


### 6. Xem lịch sử đặt bàn
**`GET /api/v1/bookings/my-history`**

**Query Parameters:**

| Param    | Type      | Required | Default | Mô tả           |
| -------- | --------- | -------- | ------- | ---------------- |
| `page`   | `integer` | No       | `0`     | Số trang         |
| `limit`   | `integer` | No       | `10`    | Số phần tử/trang |
| `status`   | `string` | No       | `null`    | Lọc theo trạng thái (SUCCESS, CANCELLED, REJECTED) |
| `fromDate/toDate`   | `integer` | No       | `null`    | Lọc theo thời gian |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": [
    {
      "bookingId": 5001,
      "restaurantName": "Haidilao - Hùng Vương Plaza",
      "restaurantLogo": "https://cdn.com/haidilao.png",
      "restaurantAddress": "126 Hùng Vương, Q5, TP.HCM",
      "bookingDate": "2026-02-15",
      "bookingTime": "19:00",
      "guestCount": 4,
      "tableLabels": ["T1.01"],
      "bookingStatus": "CONFIRMED",
      "financials": {
        "depositAmount": 200000,
        "refundAmount": 0,
        "isRefunded": false
      },
      "createdAt": "2026-02-10T14:30:00"
    }
  ],
  "meta": {
    "page": 0,
    "limit": 10,
    "total": 1,
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

### 7. Xem đánh giá nhà hàng

**`GET /api/v1/restaurants/{restaurantId}/reviews`**

**Query Parameters:**

| Param    | Type      | Required | Default | Mô tả           |
| -------- | --------- | -------- | ------- | ---------------- |
| `page`   | `integer` | No       | `0`     | Số trang         |
| `limit`   | `integer` | No       | `10`    | Số phần tử/trang |
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
    "page": 0,
    "limit": 10,
    "total": 1,
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

### 8. Đánh giá nhà hàng

**`POST /api/v1/restaurants/{restaurantId}/reviews`**

**Path Parameters:**

| Param    | Type      | Required | Mô tả           |
| -------- | --------- | -------- | ---------------- |
| `restaurantId`   | `integer` | Yes      | ID của nhà hàng |

**Request Body:**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| `rating`     | `integer` (1-5)            |
| `comment`    | `string` (Tối đa 500 ký tự) |

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Đánh giá thành công",
  "data": {
    "reviewId": 101,
    "restaurantId": 1,
    "userId": 1,
    "rating": 5,
    "comment": "Đồ ăn ngon, phục vụ tốt",
    "createdAt": "2026-02-10T14:30:00"
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

