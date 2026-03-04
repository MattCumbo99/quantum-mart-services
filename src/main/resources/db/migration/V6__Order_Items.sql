ALTER TABLE order_items
	ADD COLUMN listing_title TEXT NOT NULL DEFAULT 'TEMP';

ALTER TABLE order_items
    ALTER COLUMN listing_title DROP DEFAULT;

ALTER TABLE order_items
	ADD COLUMN listing_description TEXT;

ALTER TABLE order_items
	ADD COLUMN listing_image_url TEXT;

ALTER TABLE order_items
	RENAME COLUMN price_each TO listing_price;

ALTER TABLE order_items
	ALTER COLUMN paid_at TYPE TIMESTAMP WITH TIME ZONE
	USING paid_at AT TIME ZONE 'UTC';
	
ALTER TABLE order_items
	ADD COLUMN shipped_on TIMESTAMP WITH TIME ZONE;
