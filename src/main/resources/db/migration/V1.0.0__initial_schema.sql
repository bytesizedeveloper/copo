-- Wallet table

CREATE TABLE wallet (
    id BIGSERIAL PRIMARY KEY,
    address VARCHAR(69) UNIQUE NOT NULL,
    public_key_encoded BYTEA NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX wallet_address_idx ON wallet(address);

-- Block table

CREATE TABLE block (
    id BIGSERIAL PRIMARY KEY,
    hash_id VARCHAR(64) UNIQUE NOT NULL,
    previous_hash_id VARCHAR(64) NOT NULL,
    height BIGINT NOT NULL,
    nonce BIGINT NOT NULL,
    difficulty SMALLINT NOT NULL,
    reward DECIMAL(17, 8) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    mined_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX block_hash_id_idx ON block(hash_id);

-- Transaction table

CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    hash_id VARCHAR(64) UNIQUE NOT NULL,
    sender_address VARCHAR(69) NOT NULL,
    recipient_address VARCHAR(69) NOT NULL,
    amount DECIMAL(17, 8) NOT NULL,
    fee DECIMAL(17, 8) NOT NULL,
    type VARCHAR(12) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    signature VARCHAR NOT NULL
);

CREATE INDEX transaction_hash_id_idx ON transaction(hash_id);

-- UTXO table

CREATE TABLE utxo (
    id BIGSERIAL PRIMARY KEY,
    transaction_hash_id VARCHAR(64) NOT NULL,
    output_index VARCHAR(2) NOT NULL,
    recipient_address VARCHAR(69) NOT NULL,
    amount DECIMAL(17, 8) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    is_spent BOOLEAN NOT NULL,
    UNIQUE (transaction_hash_id, output_index)
);

CREATE INDEX utxo_id_idx ON utxo(transaction_hash_id, output_index);

CREATE INDEX utxo_recipient_address_idx ON utxo(recipient_address);

CREATE INDEX utxo_is_spent_idx ON utxo(is_spent);
