# API Flows — backend-java

Complements [API_MAP.md](API_MAP.md) (endpoint reference). This file describes the *sequence* of calls for each user journey — what happens end-to-end, in what order, and what state changes on the server.

All paths below are relative to `/api/v1` (see API_MAP.md for the base-path note).

---

## 1. Registration & Login

```mermaid
sequenceDiagram
    participant C as Client
    participant Auth as AuthController
    participant DB as MySQL

    C->>Auth: POST /auth/register {email, password, fullName}
    Auth->>DB: insert user (status=ACTIVE, roles=[CUSTOMER])
    Auth-->>C: 201 {user}

    C->>Auth: POST /auth/login {email, password}
    Auth->>DB: verify password hash
    Auth-->>C: 200 {accessToken, refreshToken, user}

    Note over C: store accessToken (memory) + refreshToken (persisted)
```

- Every authenticated request after this sends `Authorization: Bearer <accessToken>`.
- ⚠️ Known gap: `/auth/register` currently accepts a `roles` field (see API_MAP.md §7) — until fixed, don't let the client send arbitrary roles.

## 2. Token refresh

```mermaid
sequenceDiagram
    participant C as Client
    participant Auth as AuthController

    C->>Auth: POST /auth/refresh?refreshToken=...
    Auth-->>C: 200 {accessToken, refreshToken}
    Note over Auth: refreshToken passed as query param, not JSON body
```

Client calls this reactively — on a `401` from any endpoint, refresh once, retry the original request once, then force logout on a second failure.

## 3. Session end

```mermaid
sequenceDiagram
    participant C as Client
    participant Auth as AuthController

    C->>Auth: POST /auth/logout
    Auth-->>C: 204
    Note over Auth: stateless no-op — no server-side token revocation exists.<br/>Client must discard both tokens locally.
```

## 4. Forgot / reset password

```mermaid
sequenceDiagram
    participant C as Client
    participant Auth as AuthController
    participant DB as MySQL

    C->>Auth: POST /auth/forgot-password {email}
    Auth->>DB: insert PasswordResetToken (expires in 30 min)
    Auth-->>C: 204
    Note over Auth: token is log.info'd, not emailed (no mailer wired up yet)

    C->>Auth: POST /auth/reset-password {token, newPassword}
    Auth->>DB: validate token not expired, update password hash
    Auth-->>C: 204
```

## 5. Own-profile read/update

```mermaid
sequenceDiagram
    participant C as Client
    participant U as UserController

    C->>U: GET /users/me
    U-->>C: 200 {id, email, fullName, status, roles}

    C->>U: PUT /users/me {email, fullName}
    U-->>C: 200 {updated user}
    Note over U: only email + fullName are mutable here — status/roles are locked
```

## 6. Browse products (no auth required)

```mermaid
sequenceDiagram
    participant C as Client
    participant P as ProductController

    C->>P: GET /products?search=&category=&sort=&page=
    P-->>C: 200 {page of products}

    C->>P: GET /products/slug/{slug}
    P-->>C: 200 {product detail}
```

## 7. Cart flow

```mermaid
sequenceDiagram
    participant C as Client
    participant Cart as CartController
    participant DB as MySQL

    C->>Cart: GET /cart
    Cart->>DB: find-or-create cart for current user
    Cart-->>C: 200 {items[], subtotal, totalItems}

    C->>Cart: POST /cart/items {productId, quantity}
    Cart->>DB: merge into existing line if productId already in cart
    Cart-->>C: 200 {updated cart}

    C->>Cart: PUT /cart/items/{id} {quantity}
    Cart-->>C: 200 {updated cart}

    C->>Cart: DELETE /cart/items/{id}
    Cart-->>C: 200 {updated cart}
```

The cart is resolved per-user automatically (`UserResolver` → `SecurityUtils.getCurrentUsername()`) — there's no explicit "create cart" call.

## 8. Checkout flow (cart → order)

```mermaid
sequenceDiagram
    participant C as Client
    participant Order as OrderController
    participant Cart as CartService
    participant Product as Product table
    participant DB as MySQL

    C->>Order: POST /orders/checkout {recipientName, recipientPhone, shippingAddress, note?}
    Order->>Cart: load current user's cart
    alt any line's quantity > available stock
        Order-->>C: 4xx error, no changes made
    else stock OK for every line
        Order->>DB: create Order (status=PENDING)
        loop each cart line
            Order->>DB: create OrderItem (snapshot product name + price at time of purchase)
            Order->>Product: stock -= quantity, sold += quantity
        end
        Order->>Cart: clear cart
        Order-->>C: 201 {order}
    end
```

- Snapshotting name/price into `OrderItem` means later price changes on the `Product` never retroactively affect a placed order.
- ⚠️ Known gap: no payment step — the order is created as `PENDING` and nothing calls `PaymentGateway`/`StubPaymentGateway`. If a payment step is added later, it slots in between "create Order" and "clear cart".

## 9. Order history

```mermaid
sequenceDiagram
    participant C as Client
    participant Order as OrderController

    C->>Order: GET /orders?page=&size=
    Order-->>C: 200 {page of the current user's orders}

    C->>Order: GET /orders/{id}
    Order-->>C: 200 {order} (only if it belongs to the current user, else 403/404)
```

---

## Cross-cutting notes

- **Auth on every private call**: all endpoints above except register/login/refresh/forgot-password/reset-password/product-reads require a valid `Authorization: Bearer` header; a missing/expired token returns `401`, triggering the refresh flow in §2.
- **Ownership checks**: cart and order endpoints always scope to the caller's own `userId` server-side — there is no way to pass another user's ID and read their cart/orders.
- **No cross-service transactions beyond checkout**: only checkout touches multiple tables (`Cart`, `Product`, `Order`) atomically; every other flow is a single-entity read/write.
