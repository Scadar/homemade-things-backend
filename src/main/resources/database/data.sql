CREATE TABLE users
(
    user_id           BIGINT                      NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    email             VARCHAR(255),
    username          VARCHAR(255),
    password          VARCHAR(255),
    first_name        VARCHAR(255),
    last_name         VARCHAR(255),
    is_active         BOOLEAN                     NOT NULL,
    is_email_verified BOOLEAN                     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

CREATE TABLE user_device
(
    user_device_id     BIGINT                      NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id            BIGINT                      NOT NULL,
    device_type        VARCHAR(255),
    notification_token VARCHAR(255),
    device_id          VARCHAR(255)                NOT NULL,
    is_refresh_active  BOOLEAN,
    CONSTRAINT pk_user_device PRIMARY KEY (user_device_id)
);

ALTER TABLE user_device
    ADD CONSTRAINT FK_USER_DEVICE_ON_USER FOREIGN KEY (user_id) REFERENCES users (user_id);

CREATE TABLE role
(
    role_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    role_name VARCHAR(255),
    CONSTRAINT pk_role PRIMARY KEY (role_id)
);

CREATE TABLE refresh_token
(
    token_id       BIGINT                      NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    token          VARCHAR(255)                NOT NULL,
    user_device_id BIGINT                      NOT NULL,
    refresh_count  BIGINT,
    expiry_dt      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (token_id)
);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER_DEVICE FOREIGN KEY (user_device_id) REFERENCES user_device (user_device_id);

CREATE TABLE password_reset_token
(
    token_id   BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    token_name VARCHAR(255)                NOT NULL,
    expiry_dt  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    BIGINT                      NOT NULL,
    is_active  BOOLEAN                     NOT NULL,
    is_claimed BOOLEAN                     NOT NULL,
    CONSTRAINT pk_password_reset_token PRIMARY KEY (token_id)
);

ALTER TABLE password_reset_token
    ADD CONSTRAINT uc_password_reset_token_token_name UNIQUE (token_name);

ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (user_id);

CREATE TABLE email_verification_token
(
    token_id     BIGINT                      NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    token        VARCHAR(255)                NOT NULL,
    user_id      BIGINT                      NOT NULL,
    token_status VARCHAR(255),
    expiry_dt    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_email_verification_token PRIMARY KEY (token_id)
);

ALTER TABLE email_verification_token
    ADD CONSTRAINT uc_email_verification_token_token UNIQUE (token);

ALTER TABLE email_verification_token
    ADD CONSTRAINT FK_EMAIL_VERIFICATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (user_id);

INSERT INTO role (ROLE_NAME) VALUES ('ROLE_USER');
INSERT INTO role (ROLE_NAME) VALUES ('ROLE_ADMIN');
INSERT INTO role (ROLE_NAME) VALUES ('ROLE_SELLER');