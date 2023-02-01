DROP TABLE IF EXISTS BANK_USER;
DROP TABLE IF EXISTS BANK_WALLET;
DROP TABLE IF EXISTS BANK_WALLET_MOVEMENT;

create table BANK_USER (
    USER_ID         uuid not null primary key,
    USER_NAME       VARCHAR(128) NOT NULL,
    USER_USERNAME   VARCHAR(128) NOT NULL unique,
    USER_PASS       VARCHAR(128) NOT NULL
);

create table BANK_WALLET (
    WALLET_ID    uuid not null primary key,
    OWNER_ID     uuid not null,
    IBAN         VARCHAR(18) NOT NULL unique,
    AMOUNT       DECIMAL(50,2) NOT NULL DEFAULT 0,
    CURRENCY     VARCHAR(3) NOT NULL
);

create table BANK_WALLET_MOVEMENT (
    MOVEMENT_ID     uuid not null primary key,
    WALLET_ID       uuid not null,
    AMOUNT          DECIMAL(50,2) NOT NULL,
    CONCEPT         VARCHAR(128) NOT NULL,
    MOVEMENT_DATE   TIMESTAMP WITH TIME ZONE NOT NULL
);

insert into BANK_USER  (USER_ID, USER_NAME, USER_USERNAME, USER_PASS)
values ('1ff8da4c-f43d-4312-9b5d-11d0f54c0be6', 'admin', 'admin', 'c2VjcmV0');
