CREATE TABLE reviews (
     id UUID NOT NULL,
     user_id UUID NOT NULL,
     listing_id UUID NOT NULL,
     body TEXT NOT NULL,
     score INTEGER NOT NULL,
     is_edited BOOLEAN NOT NULL DEFAULT false,
     created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
     updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

     CONSTRAINT reviews_pk PRIMARY KEY (id),

     CONSTRAINT reviews_user_id_fk
         FOREIGN KEY (user_id) REFERENCES users(id)
             ON DELETE CASCADE,

     CONSTRAINT reviews_listing_id_fk
         FOREIGN KEY (listing_id) REFERENCES item_listings(id),

     CONSTRAINT reviews_score_check CHECK (score BETWEEN 1 AND 5)
);

ALTER TABLE item_listings
    ADD COLUMN review_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE item_listings
    ADD COLUMN average_score NUMERIC(2, 1) NOT NULL DEFAULT 0.0;

ALTER TABLE item_listings
    ADD CONSTRAINT item_listings_average_score_check CHECK (average_score BETWEEN 0.0 AND 5.0);

ALTER TABLE item_listings
    ADD CONSTRAINT item_listings_review_count_check CHECK (review_count >= 0);
