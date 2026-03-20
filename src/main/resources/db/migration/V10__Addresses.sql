CREATE TABLE addresses
(
    id UUID DEFAULT gen_random_uuid() NOT NULL,
    user_id UUID NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    address_line1 TEXT NOT NULL,
    address_line2 TEXT,
    city TEXT NOT NULL,
    state TEXT NOT NULL,
    zip TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    CONSTRAINT addresses_pkey PRIMARY KEY (id),

    CONSTRAINT addresses_user_id_fk
       FOREIGN KEY (user_id)
           REFERENCES users(id)
           ON DELETE CASCADE
);