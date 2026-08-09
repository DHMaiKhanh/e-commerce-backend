# DB local để xem thử (không cần Docker)

Máy bạn **đã cài sẵn MariaDB 12.0** (tương thích MySQL) và service đang chạy ở
cổng `3306`, nên **không cần tải/cài thêm database**. Chỉ cần chạy 1 script là có
đầy đủ bảng + data giả để mở lên xem.

## 1. Chạy script tạo DB + data

Mở PowerShell tại thư mục này rồi chạy:

```powershell
cd scripts\db
.\setup-local-db.ps1
```

Script tự đọc mật khẩu từ file `.env` ở gốc project (biến `DB_PASSWORD`), nên
**không hỏi lại**. Nếu chưa có `.env`, nó sẽ hỏi mật khẩu (gõ ẩn). Muốn ép dùng
mật khẩu khác: `.\setup-local-db.ps1 -RootPassword 'matkhau'`.

Script sẽ: tạo lại database `ecommerce` → chạy 4 file migration (`V1..V4`) để tạo
toàn bộ bảng → nạp `seed_dev.sql` (data giả). Chạy lại bao nhiêu lần cũng được
(mỗi lần làm mới sạch).

## 2. Xem data bằng HeidiSQL

HeidiSQL là công cụ GUI nhẹ để "mở SQL lên coi".

- Tải tại: <https://www.heidisql.com/download.php> (bản Installer 64-bit).
- Mở HeidiSQL → **New** (session mới) với thông số:
  - Network type: **MariaDB or MySQL (TCP/IP)**
  - Hostname/IP: `127.0.0.1`
  - User: `root`
  - Password: *(mật khẩu root của bạn)*
  - Port: `3306`
- Nhấn **Open** → mở database `ecommerce` ở cây bên trái → double-click từng
  bảng (`products`, `orders`, ...) → tab **Data** để xem dữ liệu.

## 3. Tài khoản & dữ liệu giả

Toàn bộ dữ liệu giả (danh mục, sản phẩm, user, địa chỉ, voucher, đánh giá, giỏ
hàng, đơn hàng...) nằm trong `seed_dev.sql`. Mở file đó — hoặc xem trực tiếp các
bảng trong HeidiSQL — để biết chi tiết.

Mẹo đăng nhập: mọi user trong data giả đều có mật khẩu là `password`.

## Lưu ý về Flyway (khi chạy app Spring Boot sau này)

Script này tạo bảng bằng cách chạy trực tiếp các file migration (KHÔNG qua
Flyway), nên bảng `flyway_schema_history` **chưa** được tạo. App dùng
`spring.jpa.hibernate.ddl-auto=validate` + Flyway, nên khi bạn chạy app:

- **Nếu muốn app quản lý schema bằng Flyway**: cho app trỏ vào một database
  **rỗng** (ví dụ đổi tên DB, hoặc `DROP DATABASE ecommerce` rồi để app tự tạo
  lại) — Flyway sẽ chạy `V1..V4` và tạo `flyway_schema_history` chuẩn.
- **Nếu muốn app dùng luôn DB có data giả này**: cần seed thêm bảng
  `flyway_schema_history` cho khớp. Nói mình một tiếng, mình sẽ dựng bước đó.

Data giả chỉ dùng cho môi trường **local/dev**, đừng đưa lên production.
