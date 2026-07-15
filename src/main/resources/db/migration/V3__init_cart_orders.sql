CREATE TABLE carts (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL,
    created_by      VARCHAR(64)     NULL,
    updated_by      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_carts_user UNIQUE (user_id),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE cart_items (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    cart_id         BIGINT          NOT NULL,
    product_id      BIGINT          NOT NULL,
    quantity        INT             NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL,
    created_by      VARCHAR(64)     NULL,
    updated_by      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cart_items_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE orders (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    user_id             BIGINT          NOT NULL,
    status              VARCHAR(16)     NOT NULL,
    total_amount        DECIMAL(12,2)   NOT NULL,
    recipient_name      VARCHAR(128)    NOT NULL,
    recipient_phone     VARCHAR(32)     NOT NULL,
    shipping_address    VARCHAR(512)    NOT NULL,
    note                VARCHAR(512)    NULL,
    created_at          DATETIME(6)     NOT NULL,
    updated_at          DATETIME(6)     NULL,
    created_by          VARCHAR(64)     NULL,
    updated_by          VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE order_items (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    order_id        BIGINT          NOT NULL,
    product_id      BIGINT          NULL,
    product_name    VARCHAR(256)    NOT NULL,
    unit_price      DECIMAL(12,2)   NOT NULL,
    quantity        INT             NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL,
    created_by      VARCHAR(64)     NULL,
    updated_by      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE password_reset_tokens (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL,
    token           VARCHAR(128)    NOT NULL,
    expires_at      DATETIME(6)     NOT NULL,
    used            BIT             NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_password_reset_tokens_token UNIQUE (token),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
