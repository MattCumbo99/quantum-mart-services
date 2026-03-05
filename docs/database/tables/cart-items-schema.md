# Cart Items Table

----
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
- Table name: `cart_items`

| Column Name | Datatype    | Nullable | Default             | Description                                   |
|-------------|-------------|----------|---------------------|-----------------------------------------------|
| id          | PK `UUID`   | No       | `gen_random_uuid()` | Unique identifier for this cart item.         |
| user_id     | FK `UUID`   | No       |                     | The ID of the user this cart item belongs to. |
| listing_id  | FK `UUID`   | No       |                     | The ID of the listing this cart item is for.  |
| quantity    | `INT`       | No       | 1                   | Amount of this item which is in the cart.     |

## 🎯Purpose
A volatile table which holds items currently in a user's cart.

## ⏱️Lifecycle
### ➕Row Creation
When a user clicks "add to cart" on an item listing.

### 🔄Row Updates
- `quantity` is increased when the user tries to add an item in their cart that already exists.

### 🗑️Row Deletion
1. The user manually removes the item, either individually or in bulk.
2. When an **order** is created, all cart items associated with the user are removed.
3. The listing associated with the cart item is deleted.
4. The linked user entity is deleted.

## 📌Important Columns
- `user_id` - Determines which user's cart this item is in.
- `listing_id` - Holds item data.

## 🤝Relationships
- Belongs to: `users` - Users add items to their cart.
- Has many: `item_listings` - A cart can store information about multiple listings to be purchased.

## 🔒Invariants
1. `quantity` must be greater than 0.
2. `listing_id` always points to an existing item listing.
3. No entry can have the same combination of `user_id` and `listing_id`.

## 🔍Access Patterns
- Fetch all cart items by user.

## ⚙️Operational Notes
None.
