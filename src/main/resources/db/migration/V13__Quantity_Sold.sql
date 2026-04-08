ALTER TABLE item_listings
    ADD COLUMN quantity_sold INTEGER NOT NULL DEFAULT 0;

ALTER TABLE item_listings
    ADD CONSTRAINT item_listings_quantity_sold_positive CHECK (quantity_sold >= 0);
