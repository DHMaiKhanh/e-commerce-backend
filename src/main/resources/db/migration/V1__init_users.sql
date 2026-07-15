CREATE TABLE users (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    username        VARCHAR(64)     NOT NULL,
    email           VARCHAR(128)    NOT NULL,
    password_hash   VARCHAR(128)    NOT NULL,
    full_name       VARCHAR(128)    NULL,
    status          VARCHAR(16)     NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NULL,
    created_by      VARCHAR(64)     NULL,
    updated_by      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id BIGINT      NOT NULL,
    role    VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_users_status ON users (status);
CREATE INDEX idx_users_created_at ON users (created_at);
