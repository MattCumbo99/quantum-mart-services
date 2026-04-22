ALTER TABLE cart_items
    ALTER COLUMN user_id DROP NOT NULL;

ALTER TABLE cart_items
    ADD COLUMN guest_session_id UUID;

ALTER TABLE cart_items
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now();

ALTER TABLE cart_items
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now();

ALTER TABLE cart_items
    ADD CONSTRAINT cart_items_identity_xor
        CHECK (
                (user_id IS NOT NULL AND guest_session_id IS NULL) OR
                (user_id IS NULL AND guest_session_id IS NOT NULL)
            );

ALTER TABLE orders
    ALTER COLUMN buyer_id DROP NOT NULL;

ALTER TABLE orders
    ADD COLUMN guest_email TEXT;

ALTER TABLE orders
    ADD CONSTRAINT orders_identity_xor
        CHECK (
                (buyer_id IS NOT NULL AND guest_email IS NULL) OR
                (buyer_id IS NULL AND guest_email IS NOT NULL)
            );
