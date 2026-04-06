CREATE TABLE currency (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(3)   NOT NULL UNIQUE,
    name       VARCHAR(50)  NOT NULL,
    symbol     VARCHAR(5)   NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(150) NOT NULL UNIQUE,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE account (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    type        ENUM('CHECKING', 'SAVINGS', 'CASH', 'CREDIT_CARD', 'INVESTMENT') NOT NULL,
    currency_id BIGINT       NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    user_id     BIGINT       NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_currency FOREIGN KEY (currency_id) REFERENCES currency (id),
    CONSTRAINT fk_account_user     FOREIGN KEY (user_id)     REFERENCES user (id)
);

CREATE TABLE category (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    user_id    BIGINT       NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE transfer (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_amount      DECIMAL(19, 4) NOT NULL,
    from_currency_id BIGINT         NOT NULL,
    to_amount        DECIMAL(19, 4) NOT NULL,
    to_currency_id   BIGINT         NOT NULL,
    exchange_rate    DECIMAL(19, 6) NOT NULL DEFAULT 1.000000,
    date             DATE           NOT NULL,
    description      VARCHAR(255),
    from_account_id  BIGINT         NOT NULL,
    to_account_id    BIGINT         NOT NULL,
    user_id          BIGINT         NOT NULL,
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transfer_from_currency FOREIGN KEY (from_currency_id) REFERENCES currency (id),
    CONSTRAINT fk_transfer_to_currency   FOREIGN KEY (to_currency_id)   REFERENCES currency (id),
    CONSTRAINT fk_transfer_from_account  FOREIGN KEY (from_account_id)  REFERENCES account (id),
    CONSTRAINT fk_transfer_to_account    FOREIGN KEY (to_account_id)    REFERENCES account (id),
    CONSTRAINT fk_transfer_user          FOREIGN KEY (user_id)          REFERENCES user (id)
);

CREATE TABLE transaction (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    type                 ENUM('INCOME', 'EXPENSE') NOT NULL,
    original_amount      DECIMAL(19, 4) NOT NULL,
    original_currency_id BIGINT         NOT NULL,
    reference_amount     DECIMAL(19, 4) NOT NULL,
    reference_currency_id BIGINT        NOT NULL,
    exchange_rate        DECIMAL(19, 6) NOT NULL DEFAULT 1.000000,
    date                 DATE           NOT NULL,
    description          VARCHAR(255),
    account_id           BIGINT         NOT NULL,
    category_id          BIGINT,
    user_id              BIGINT         NOT NULL,
    created_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_original_currency  FOREIGN KEY (original_currency_id)  REFERENCES currency (id),
    CONSTRAINT fk_transaction_reference_currency FOREIGN KEY (reference_currency_id) REFERENCES currency (id),
    CONSTRAINT fk_transaction_account            FOREIGN KEY (account_id)            REFERENCES account (id),
    CONSTRAINT fk_transaction_category           FOREIGN KEY (category_id)           REFERENCES category (id),
    CONSTRAINT fk_transaction_user               FOREIGN KEY (user_id)               REFERENCES user (id)
);

CREATE TABLE recurring_transaction (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    type                 ENUM('INCOME', 'EXPENSE')                    NOT NULL,
    original_amount      DECIMAL(19, 4)                               NOT NULL,
    original_currency_id BIGINT                                       NOT NULL,
    frequency            ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
    next_execution_date  DATE                                         NOT NULL,
    end_date             DATE,
    description          VARCHAR(255),
    active               BOOLEAN                                      NOT NULL DEFAULT TRUE,
    account_id           BIGINT                                       NOT NULL,
    category_id          BIGINT,
    user_id              BIGINT                                       NOT NULL,
    created_at           DATETIME                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_recurring_currency  FOREIGN KEY (original_currency_id) REFERENCES currency (id),
    CONSTRAINT fk_recurring_account   FOREIGN KEY (account_id)           REFERENCES account (id),
    CONSTRAINT fk_recurring_category  FOREIGN KEY (category_id)          REFERENCES category (id),
    CONSTRAINT fk_recurring_user      FOREIGN KEY (user_id)              REFERENCES user (id)
);
