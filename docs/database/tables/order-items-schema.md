# Order Items Table

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
- Table name: `order_items`

| Column Name         | Datatype                   | Nullable | Default               | Description                                      |
|---------------------|----------------------------|----------|-----------------------|--------------------------------------------------|
| id                  | PK `UUID`                  | No       | `gen_random_uuid()`   | Identifier for the order item.                   |
| order_id            | FK `UUID`                  | No       |                       | ID of the original order this item is a part of. |
| listing_id          | FK `UUID`                  | No       |                       | ID of the listing this item originates from.     |
| seller_id           | FK `UUID`                  | No       |                       | User ID of the original seller.                  |
| quantity            | `INT`                      | No       |                       | Amount of this item in the order.                |
| listing_price       | `NUMERIC(10,2)`            | No       |                       | The original price this item was bought at.      |
| status              | `VARCHAR(50)`              | No       | PAID_PENDING_SHIPMENT | The status on the item.                          |
| paid_at             | `TIMESTAMP WITH TIME ZONE` | No       | `now()`               | When the payment was made for the item.          |
| listing_title       | `TEXT`                     | No       |                       | The listing's original title.                    |
| listing_description | `TEXT`                     | Yes      |                       | The listing's original image URL.                |
| shipped_on          | `TIMESTAMP WITH TIME ZONE` | Yes      |                       | When the item was shipped.                       |

## 🎯Purpose
Keeps track of individual items bought within an order.

## ⏱️Lifecycle
### ➕Row Creation
When an order is created, all items from the user's **cart** will be added as order item entities.

### 🔄Row Updates
- `status` changes when the seller manually sets the item as either _SHIPPED_ or _CANCELLED_.
- `shipped_on` receives a value when the **status** field changes to _SHIPPED_ for the first time.

### 🗑️Row Deletion
Entities are permanent to preserve financial history.

## 📌Important Columns
- `order_id` - The order this item belongs to.
- `status` - Contributes to the overall status of an order and how funds are transferred.

## 🤝Relationships
- Belongs to: `orders` - Order items always belong to one order.

## 🔒Invariants
1. `status` must have one of the following values:
    - _**PAID_PENDING_SHIPMENT**_ - Item was purchased and is waiting for the seller to update the status.
    - _**SHIPPED**_ - Item was packaged and shipped to the address specified on the order.
    - _**COMPLETED**_ - Item was delivered to the shipping address on the order.
    - _**CANCELLED**_ - The seller declines to fulfill the contract and cancels the transaction.
    - _**REFUNDED**_ - Manually set by Quantum Mart employees due to a conflict in the transaction.
2. `status` can only be modified by the original seller and when the status is _**PAID_PENDING_SHIPMENT**_.
3. `listing_price` must be greater than or equal to 0.
4. `quantity` must be greater than 0.

## 🔍Access Patterns
- Fetch order items by seller where either no items OR at least 1 item has a status of _**PAID_PENDING_SHIPMENT**_.
- Fetch order items by order ID.

## ⚙️Operational Notes
None.