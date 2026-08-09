-- V4: Align schema with frontend (frontend-react) data model.
-- Adds: category image/order, user phone/avatar, order payment/code/fee/discount,
--        reviews, vouchers, user_addresses.

-- ---------------------------------------------------------------------------
-- 1) Categories: image + display order (homepage "Danh mục nổi bật" thumbnails)
-- ---------------------------------------------------------------------------
ALTER TABLE categories
    ADD COLUMN image_url VARCHAR(512) NULL,
    ADD COLUMN position  INT          NOT NULL DEFAULT 0;

CREATE INDEX idx_categories_position ON categories (position);

-- ---------------------------------------------------------------------------
-- 2) Users: phone + avatar (frontend User type)
-- ---------------------------------------------------------------------------
ALTER TABLE users
    ADD COLUMN phone      VARCHAR(32)  NULL,
    ADD COLUMN avatar_url VARCHAR(512) NULL;

-- ---------------------------------------------------------------------------
-- 3) User addresses (structured province/district/ward + saved addresses)
-- ---------------------------------------------------------------------------
CREATE TABLE user_addresses (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL,
    full_name       VARCHAR(128)    NOT NULL,
    phone           VARCHAR(32)     NOT NULL,
    province        VARCHAR(128)    NOT NULL,
    district        VARCHAR(128)    NOT NULL,
    ward            VARCHAR(128)    NOT NULL,
    address_line    VARCHAR(512)    NOT NULL,
    is_default      BIT             NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL,
    created_by      VARCHAR(64)     NULL,
    updated_by      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_addresses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_user_addresses_user_id ON user_addresses (user_id);

-- ---------------------------------------------------------------------------
-- 4) Reviews (source for products.rating / products.review_count)
-- ---------------------------------------------------------------------------
CREATE TABLE reviews (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    product_id      BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    rating          INT             NOT NULL,
    comment         TEXT            NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL,
    created_by      VARCHAR(64)     NULL,
    updated_by      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_reviews_product_user UNIQUE (product_id, user_id),
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_reviews_product_id ON reviews (product_id);
CREATE INDEX idx_reviews_user_id ON reviews (user_id);

-- ---------------------------------------------------------------------------
-- 5) Vouchers (homepage "Giảm 100K" / "Freeship" promotions)
-- ---------------------------------------------------------------------------
CREATE TABLE vouchers (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    code                VARCHAR(64)     NOT NULL,
    description         VARCHAR(256)    NULL,
    discount_type       VARCHAR(16)     NOT NULL,
    discount_value      DECIMAL(12,2)   NOT NULL,
    min_order_amount    DECIMAL(12,2)   NOT NULL DEFAULT 0,
    max_discount_amount DECIMAL(12,2)   NULL,
    usage_limit         INT             NULL,
    used_count          INT             NOT NULL DEFAULT 0,
    free_shipping       BIT             NOT NULL DEFAULT 0,
    active              BIT             NOT NULL DEFAULT 1,
    starts_at           DATETIME(6)     NULL,
    expires_at          DATETIME(6)     NULL,
    created_at          DATETIME(6)     NOT NULL,
    updated_at          DATETIME(6)     NULL,
    created_by          VARCHAR(64)     NULL,
    updated_by          VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_vouchers_code UNIQUE (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_vouchers_active ON vouchers (active);

-- ---------------------------------------------------------------------------
-- 6) Orders: code, payment method, subtotal/shipping fee/discount, voucher link
--    (new NOT NULL columns carry defaults so existing rows/code stay valid)
-- ---------------------------------------------------------------------------
ALTER TABLE orders
    ADD COLUMN code            VARCHAR(32)     NULL,
    ADD COLUMN payment_method  VARCHAR(16)     NOT NULL DEFAULT 'COD',
    ADD COLUMN subtotal        DECIMAL(12,2)   NOT NULL DEFAULT 0,
    ADD COLUMN shipping_fee    DECIMAL(12,2)   NOT NULL DEFAULT 0,
    ADD COLUMN discount_amount DECIMAL(12,2)   NOT NULL DEFAULT 0,
    ADD COLUMN voucher_id      BIGINT          NULL,
    ADD CONSTRAINT uk_orders_code UNIQUE (code),
    ADD CONSTRAINT fk_orders_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers (id) ON DELETE SET NULL;

CREATE INDEX idx_orders_code ON orders (code);
