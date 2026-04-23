# Orders Table

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
- Table name: `orders`

| Column Name        | Datatype                   | Nullable | Default             | Description                                    |
|--------------------|----------------------------|----------|---------------------|------------------------------------------------|
| id                 | PK `UUID`                  | No       | `gen_random_uuid()` | Identifier for the order.                      |
| buyer_id           | FK `UUID`                  | Yes      |                     | User ID of the user who made the order.        |
| guest_email        | `TEXT`                     | Yes      |                     | Email for non-user.                            |
| status             | `VARCHAR(50)`              | No       | pending             | Status of the order.                           |
| total_paid         | `NUMERIC(10,2)`            | No       |                     | How much the buyer paid in total.              |
| created_at         | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             | When the order was made.                       |
| shipping_firstname | `VARCHAR(100)`             | No       |                     | First Name of the buyer.                       |
| shipping_lastname  | `VARCHAR(100)`             | No       |                     | Last Name of the buyer.                        |
| shipping_address1  | `VARCHAR(150)`             | No       |                     | Shipping address details: Street, number, etc. |
| shipping_address2  | `VARCHAR(50)`              | Yes      | `null`              | Second part of the address, such as Apt 2.     |
| shipping_city      | `VARCHAR(100)`             | No       |                     | City to ship the items to.                     |
| shipping_state     | `VARCHAR(50)`              | No       |                     | U.S. State to ship the items to.               |
| shipping_zip       | `VARCHAR(10)`              | No       |                     | ZIP code of the shipment.                      |
| shipping_phone     | `VARCHAR(20)`              | No       |                     | Phone number associated with the order.        |

## 🎯Purpose
An **order** entity represents a finalized purchase initiated by a buyer. It captures the high‑level details of a 
transaction at the moment checkout is completed, including the buyer, pricing totals, and the overall lifecycle 
state of the order. Each order acts as the parent record for the individual items purchased (represented by the order 
items table).

## ⏱️Lifecycle
### ➕Row Creation
An order is created when a user provides shipping information and confirms payment at cart checkout.

### 🔄Row Updates
The order `status` will be updated from _PENDING_ to _COMPLETED_ when all associated **order items** have all reached
SHIPPED status.

### 🗑️Row Deletion
Explain whether rows are permanent, soft-deleted, or cleaned up.

## 📌Important Columns
- `buyer_id` - Deducts the `total_paid` value from this user's balance.
- `status` -  Tells the buyer if all items have been shipped.

## 🤝Relationships
- Belongs to: **users** - Only users can create an order.
- Has many: **order items** - Individual items associated with the order.

## 🔒Invariants
1. `total_paid` - Must be a positive number greater than 0.
2. `status` - Must be one of the following values:
   1. _PENDING_ - There are order items that haven't shipped.
   2. _COMPLETED_ - All items have been shipped.
3. `guest_email` must be provided if a non-user is creating the order.
4. `buyer_id` must be null if `guest_email` was provided.

## 🔍Access Patterns
- Fetch all orders via `buyer_id` and `status`

## ⚙️Operational Notes
None.