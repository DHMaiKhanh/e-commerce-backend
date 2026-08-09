---
name: ship
description: Commit the current working tree and push it to GitHub. Use when the user wants to commit + push their changes, e.g. "push code lên github", "commit rồi push", "ship it", or "/ship". Handles staging, a conventional commit message, schema-migration discipline, and excluding temporary test artifacts.
---

# /ship — commit + push lên GitHub

Mục tiêu: đóng gói các thay đổi hiện tại thành một commit sạch và push lên `origin`.
Chạy tuần tự các bước dưới đây; dừng lại hỏi người dùng khi gặp mục có ghi "HỎI".

## 1. Khảo sát trạng thái

- `git status` và `git diff` (cả `git diff --staged`) để hiểu toàn bộ thay đổi.
- `git log --oneline -5` để bắt đúng phong cách commit message của repo.
- `git remote -v` và `git branch` để biết đang ở branch nào và remote nào.

## 2. Lọc file KHÔNG nên commit

Không stage các artifact tạm / debug. Nếu chúng xuất hiện, thêm vào `.gitignore`
thay vì commit:

- `.playwright-mcp/` — log của Playwright MCP smoke test
- `*.png` ảnh chụp màn hình test (vd `smoke-test-*.png`)
- File môi trường / bí mật: `.env`, `application-local.yml`
- `target/`, `*.class`, `logs/` (thường đã nằm trong `.gitignore`)

Nếu không chắc một file có thuộc source thật hay không → **HỎI** trước khi stage.

## 3. Kỷ luật schema ↔ migration (quan trọng với repo này)

Repo dùng **MySQL/MariaDB + Flyway** và `spring.jpa.hibernate.ddl-auto=validate`.
Vì vậy:

- Nếu diff **đổi/thêm entity** (`src/main/java/.../entity/*.java`) hoặc enum được
  persist, PHẢI có migration Flyway tương ứng (`src/main/resources/db/migration/V*.sql`)
  thêm/sửa đúng cột. Không có migration ⇒ app sẽ fail `validate` khi khởi động.
- Nếu thiếu migration ⇒ **HỎI** người dùng có muốn dừng để bổ sung không, đừng
  âm thầm push.
- Không nhúng dữ liệu seed/DB vào tài liệu; docs chỉ trỏ tới file `.sql`.

## 4. (Tuỳ chọn) build kiểm tra nhanh

Nếu thay đổi chạm code Java và người dùng không vội, gợi ý build trước khi push:

```bash
./mvnw -q -DskipTests compile
```

Không tự ý chạy test dài; hỏi nếu muốn `-DskipTests=false`.

## 5. Stage + commit

- Stage đúng các file thuộc thay đổi (dùng `git add <path>` cụ thể, tránh
  `git add -A` khi còn artifact chưa lọc).
- Viết commit message theo quy ước hiện tại của repo:
  - Dòng tiêu đề ngắn gọn, thể mệnh lệnh (vd `feat: align schema with frontend`).
  - Thân bài (nếu cần) giải thích *tại sao*, không chỉ *cái gì*.
  - Kết thúc message bằng trailer:

    ```
    Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
    ```

- Nếu đang ở branch mặc định (`main`) và thay đổi lớn/rủi ro → cân nhắc tạo branch
  mới rồi mở PR. Với repo cá nhân một người, commit thẳng `main` chấp nhận được
  nếu người dùng đã yêu cầu push trực tiếp — nhưng nếu không chắc thì **HỎI**.

## 6. Push

- `git push` (hoặc `git push -u origin <branch>` cho branch mới).
- Nếu push bị từ chối do non-fast-forward → `git pull --rebase` rồi push lại; báo
  người dùng nếu có xung đột.
- Sau khi push, in ra hash commit + tên branch để người dùng đối chiếu. Nếu đã tạo
  branch mới, gợi ý lệnh/đường dẫn mở PR bằng `gh`.

## Ghi chú

- Chỉ push khi người dùng đã yêu cầu (skill này chính là yêu cầu đó).
- Không dùng `--no-verify` hay bỏ qua hook trừ khi người dùng yêu cầu rõ.
- Không force-push (`--force`) trừ khi thực sự cần và đã xác nhận.
