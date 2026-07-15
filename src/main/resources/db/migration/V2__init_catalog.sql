CREATE TABLE categories (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(128)    NOT NULL,
    slug            VARCHAR(128)    NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL,
    created_by      VARCHAR(64)     NULL,
    updated_by      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_categories_slug UNIQUE (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE products (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(256)    NOT NULL,
    slug            VARCHAR(256)    NOT NULL,
    description     TEXT            NULL,
    price           DECIMAL(12,2)   NOT NULL,
    sale_price      DECIMAL(12,2)   NULL,
    stock           INT             NOT NULL,
    category_id     BIGINT          NULL,
    rating          DOUBLE          NOT NULL DEFAULT 0,
    review_count    INT             NOT NULL DEFAULT 0,
    sold            INT             NOT NULL DEFAULT 0,
    location        VARCHAR(128)    NULL,
    is_official     BIT             NOT NULL DEFAULT 0,
    free_shipping   BIT             NOT NULL DEFAULT 0,
    featured        BIT             NOT NULL DEFAULT 0,
    status          VARCHAR(16)     NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL,
    created_by      VARCHAR(64)     NULL,
    updated_by      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_products_slug UNIQUE (slug),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE product_images (
    product_id  BIGINT          NOT NULL,
    position    INT             NOT NULL,
    url         VARCHAR(512)    NOT NULL,
    PRIMARY KEY (product_id, position),
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_status ON products (status);
CREATE INDEX idx_products_featured ON products (featured);
CREATE INDEX idx_categories_slug ON categories (slug);
