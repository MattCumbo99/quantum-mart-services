# Item Listings Table

## Table of Contents
- [Schema](#Schema)
- [Purpose](#purpose)
- [Lifecycle](#lifecycle)
    - [Row Creation](#row-creation)
    - [Row Updates](#row-updates)
    - [Row Deletion](#row-deletion)
- [Important Columns](#important-columns)
- [Relationships](#relationships)
- [Invariants](#invariants)
- [Access Patterns](#access-patterns)
- [Operational Notes](#operational-notes)

## 📄Schema
- Table name: `item_listings`

| Column Name | Datatype                   | Nullable | Default             | Description                        |
|-------------|----------------------------|----------|---------------------|------------------------------------|
| id          | PK `UUID`                  | No       | `gen_random_uuid()` | Unique ID for the listing.         |
| seller_id   | FK `UUID`                  | No       |                     | User ID of user selling the item.  |
| title       | `VARCHAR(200)`             | No       |                     | Name of the listing.               |
| description | `TEXT`                     | Yes      |                     | Description of the listing.        |
| price       | `NUMERIC(10,2)`            | No       |                     | How much the item costs.           |
| image_url   | `TEXT`                     | Yes      |                     | Image URL of the product.          |
| created_at  | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             | When the listing was created.      |
| updated_at  | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             | When the listing was last updated. |
| is_active   | `BOOLEAN`                  | No       | true                | If the listing is for sale.        |

## 🎯Purpose
Stores information on products currently being sold on Quantum Mart.

## ⏱️Lifecycle
### ➕Row Creation
When a logged-in user submits a form containing information for a new item listing.

### 🔄Row Updates
Users can update their item listings any time by setting a new title, description, price, or if it is active.

### 🗑️Row Deletion
No entries are hard-deleted. They are set to "not active" and instead is removed visibly except from the seller.

## 📌Important Columns
- `id` - Primary key to access the item listing with.
- `seller_id` - The user `id` this item listing belongs to.

## 🤝Relationships
- Belongs to: **users** - The user that created the item listing is responsible for facilitating **order** progress on any purchase of 
  the listing.

## 🔒Invariants
1. `price` must be a number greater than zero.
2. `seller_id` must always point to a valid user `id`. The row is deleted automatically otherwise.

## 🔍Access Patterns
- Fetch item listings by `seller_id`.
- Fetch ALL item listings.
- Create an item listing using a valid user `id`.

## ⚙️Operational Notes
None.