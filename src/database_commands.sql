CREATE TABLE auctions (
    auction_id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    fare DECIMAL(10, 2) NOT NULL CHECK (fare > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cards (
    card_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    holder_name VARCHAR(100) NOT NULL,
    expiration_month INTEGER NOT NULL CHECK (expiration_month BETWEEN 1 AND 12),
    expiration_year INTEGER NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0),
    blocked_sum DECIMAL(15, 2) NOT NULL DEFAULT 0.00 CHECK (blocked_sum >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT valid_expiration CHECK (
        (expiration_year > EXTRACT(YEAR FROM CURRENT_DATE)) OR
        (expiration_year = EXTRACT(YEAR FROM CURRENT_DATE) AND
        (expiration_month > EXTRACT(MONTH FROM CURRENT_DATE))
    )
);

CREATE TABLE items (
    item_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    auction_id UUID NOT NULL,
    card_id UUID NOT NULL,
    description TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (auction_id) REFERENCES auctions(auction_id) ON DELETE CASCADE,
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE
);

CREATE TABLE bids (
    bid_id UUID PRIMARY KEY,
    item_id UUID NOT NULL,
    user_id UUID NOT NULL,
    card_id UUID NOT NULL,
    bid_sum DECIMAL(15, 2) NOT NULL CHECK (bid_sum > 0),
    bid_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE
);