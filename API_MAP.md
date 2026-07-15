# API Map — backend-java

Source: `D:\Project\E-commerce\backend-java`
Framework: Spring Boot 3.3.5, Java 17, MySQL + Flyway, JWT (jjwt 0.12.6), stateless Spring Security.
Base path: `server.servlet.context-path=/api` + `AppConstants.API_V1=/v1` → **all endpoints are under `/api/v1/...`**.

> Note: the frontend's `src/api/endpoints.ts` lists bare paths (e.g. `/auth/login`). The frontend axios `baseURL` must include `/api` (or `/api/v1` with paths adjusted) for requests to reach this backend.

## 1. Auth — `AuthController` (`/v1/auth`)

| Method | Path | Auth | Status |
|---|---|---|---|
| POST | `/login` | public | ✅ existing |
| POST | `/refresh` | public | ✅ existing (`refreshToken` as query param, not JSON body) |
| POST | `/register` | public | ✅ existing (reuses `CreateUserRequest`, requires a `roles` field — a public caller can currently request arbitrary roles; consider a dedicated `RegisterRequest` that always assigns `ROLE_CUSTOMER` server-side) |
| GET | `/me` | authenticated | ✅ **added** — returns current user via `AuthService.getCurrentUser()` |
| POST | `/logout` | public | ✅ **added** — stateless no-op (204); no refresh-token revocation store exists, so this only tells the client to discard tokens |
| POST | `/forgot-password` | public | ✅ **added** — issues a `PasswordResetToken` (30 min TTL), logged via `log.info` (no email service wired up yet — swap the `log.info` in `AuthServiceImpl.forgotPassword` for a real mailer when available) |
| POST | `/reset-password` | public | ✅ **added** — validates token + expiry, updates password hash |

## 2. Users — `UserController` (`/v1/users`)

| Method | Path | Auth | Status |
|---|---|---|---|
| GET | `/me` | authenticated | ✅ **added** — self-service profile read |
| PUT | `/me` | authenticated | ✅ **added** — self-service profile update (`UpdateProfileRequest`: email + fullName only, cannot change own `status`/`roles`) |
| POST | `` | ADMIN | ✅ existing |
| GET | `/{id}` | ADMIN, STAFF | ✅ existing |
| GET | `` | ADMIN, STAFF | ✅ existing (search) |
| PUT | `/{id}` | ADMIN | ✅ existing |
| DELETE | `/{id}` | ADMIN | ✅ existing (soft delete) |

## 3. Products — `ProductController` (`/v1/products`)

All public reads, ADMIN writes — unchanged, already matched the frontend 1:1.

| Method | Path | Status |
|---|---|---|
| GET | `` (search/filter/sort/paginate) | ✅ existing |
| GET | `/featured` | ✅ existing |
| GET | `/categories` | ✅ existing |
| GET | `/slug/{slug}` | ✅ existing |
| GET | `/{id}` | ✅ existing |
| POST / PUT / DELETE | ADMIN only | ✅ existing |

## 4. Cart — `CartController` (`/v1/cart`) — **new, fully implemented**

Backed by new `Cart`/`CartItem` entities (one cart per user, auto-created on first access).

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `` | authenticated | Get current user's cart |
| POST | `/items` | authenticated | Add product+quantity (merges into existing line if already present) |
| PUT | `/items/{id}` | authenticated | Set quantity on a cart item |
| DELETE | `/items/{id}` | authenticated | Remove a cart item |
| DELETE | `` | authenticated | Clear the cart |

`CartService`/`CartServiceImpl` resolve the current user via the shared `UserResolver` (`SecurityUtils.getCurrentUsername()` → `UserRepository`).

## 5. Orders — `OrderController` (`/v1/orders`) — **new, fully implemented**

Backed by new `Order`/`OrderItem` entities.

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `` | authenticated | Paginated list of the current user's orders |
| GET | `/{id}` | authenticated | Single order (must belong to current user) |
| POST | `/checkout` | authenticated | Converts the current cart into an `Order`: validates stock per line, snapshots product name/price into `OrderItem`, decrements `Product.stock`, increments `Product.sold`, clears the cart, sets `Order.status = PENDING` |

`CheckoutRequest` requires `recipientName`, `recipientPhone`, `shippingAddress` (+ optional `note`). No payment integration is wired into checkout yet — `integration/payment/StubPaymentGateway` exists but is unused; checkout currently only creates the order record.

## 6. New Flyway migration

`V3__init_cart_orders.sql` adds: `carts`, `cart_items`, `orders`, `order_items`, `password_reset_tokens` tables (FKs to `users`/`products`, cascade deletes on parent removal). Required because `spring.jpa.hibernate.ddl-auto=validate` — schema only comes from Flyway, never auto-generated.

## 7. Remaining known gaps (not addressed in this pass)

- **Register DTO reuse**: `POST /auth/register` accepts a `roles` field via `CreateUserRequest` — a public registration endpoint that lets the caller pick their own roles is a privilege-escalation risk. Should be split into a `RegisterRequest` (no roles field) that always assigns `ROLE_CUSTOMER`.
- **Refresh token transport**: `POST /auth/refresh` takes `refreshToken` as a `@RequestParam`, not a JSON body — confirm the frontend's `authService.refresh()` call matches, or add a body-based DTO.
- **No payment gateway wiring**: checkout creates a `PENDING` order but never calls `PaymentGateway`/`StubPaymentGateway`.
- **No admin order/user management UI-facing endpoints beyond what already existed** (e.g., admin order status transitions) — out of scope for the current frontend gap list.
