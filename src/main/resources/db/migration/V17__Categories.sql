CREATE TABLE categories (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    slug TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    CONSTRAINT categories_pk PRIMARY KEY (id),
    CONSTRAINT categories_slug_unique UNIQUE (slug),
    CONSTRAINT categories_slug_regex_check CHECK (slug ~ '^[a-z0-9-]+$')
);

INSERT INTO categories (id, name, slug, is_active)
    VALUES ('e87630d5-762a-45e4-8780-cafd2922b8ab', 'Uncategorized', 'uncategorized', false);

ALTER TABLE item_listings
    ADD COLUMN category_id UUID NOT NULL DEFAULT 'e87630d5-762a-45e4-8780-cafd2922b8ab';

ALTER TABLE item_listings
    ADD CONSTRAINT item_listings_categories_fk FOREIGN KEY (category_id) REFERENCES categories(id);
