<!--
╔══════════════════════════════════════════════════════════════╗
║  TEMPLATE TRỐNG                                              ║
╚══════════════════════════════════════════════════════════════╝
-->

<!--

## N. Module: [Tên Module]

### N.1. [Tên API]

**`METHOD /endpoint`**

| Thuộc tính   | Giá trị                    |
| ------------ | -------------------------- |
| **Summary**  | Mô tả ngắn gọn            |
| **Auth**     | Bearer Token / No |
| **Role**     | `ADMIN`, `STAFF`           |

**Path Parameters:** *(nếu có)*

| Param | Type      | Required | Mô tả     |
| ----- | --------- | -------- | ---------- |
| `id`  | `integer` | Yes       | ID của ... |

**Query Parameters:** *(nếu có)*

| Param    | Type      | Required | Default | Mô tả           |
| -------- | --------- | -------- | ------- | ---------------- |
| `page`   | `integer` | No       | `0`     | Số trang         |
| `size`   | `integer` | No       | `10`    | Số phần tử/trang |

**Request Body:** *(nếu có)*

| Field   | Type     | Required | Validation    | Mô tả   |
| ------- | -------- | -------- | ------------- | -------- |
| `field` | `string` | Yes       | max 255 ký tự | Mô tả   |

```json
{
  "field": "value"
}
```

**Response `200 OK`:**

```json
{
  "status": 200,
  "message": "Mô tả",
  "data": { }
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

-->