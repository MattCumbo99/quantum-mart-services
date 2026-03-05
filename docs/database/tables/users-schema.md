# Users Table

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
- Table name: `users`

| Column Name   | Datatype        | Nullable | Default             | Description                       |
|---------------|-----------------|----------|---------------------|-----------------------------------|
| id            | PK `UUID`       | No       | `gen_random_uuid()` | Primary identifier for this user. |
| username      | `VARCHAR(50)`   | No       |                     | Text identifier for the user.     |
| password_hash | `VARCHAR(255)`  | No       |                     | The user's hashed password.       |
| email         | `VARCHAR(255)`  | Yes      |                     | Email associated with this user.  |
| balance       | `NUMERIC(12,2)` | No       | 1000.00             | Amount of currency this user has. |
| role          | `VARCHAR(50)`   | No       | user                | Level of privilege this user has. |
| created_at    | `TIMESTAMP`     | No       | `now()`             | When this user was created.       |

## 🎯Purpose
Stores and provides information related to a client using the application.

## ⏱️Lifecycle
### ➕Row Creation
1. Non-user fills out a registration form.
2. If the form is valid and the username requested does not exist yet, the `user` entity is created.

### 🔄Row Updates
1. The user's `balance` field will be updated during the lifecycle of an **order**:
    1. When the user places an order with the items in their cart, the **total amount** of the order is subtracted from the
    balance.
    2. When an order is placed that contains an item listing sold by the user, the amount paid for the item will be added
    to the seller's balance **only when the order item is shipped**.

### 🗑️Row Deletion
Every entity is permanent.

## 📌Important Columns
- `id` - A static reference to the user object. This is immutable and will never change.
- `username` - Mostly used as a way to visually identify the user.
- `role` - Provides access to certain controller functions and API calls depending on their privilege level.

## 🤝Relationships
- Has many: **item listings** - Users can sell many items.
- Has many: **cart items** - Multiple items can be in a user's cart.
- Has many: **orders** - Users can place multiple orders.

## 🔒Invariants
1. Every `username` is **unique**. Casing does not apply to uniqueness.
   - Example: "Test" will not be allowed if another username "TEST" exists.
2. The user's `username` abides by this regular expression: `^[A-Za-z0-9_-]+$`
   - Uppercase and lowercase letters
   - Any number
   - Underscores and dashes
3. The user's `role` matches one of the following values (permissions are applied via hierarchy):
   1. _user_ - Allows basic functionality.
   2. _moderator_ - Provides basic administrative access.
   3. _admin_ - Can promote users into moderators and modify balances.
   4. _superadmin_ - Can promote moderators into admins. There should only be **one** user with this role.

## 🔍Access Patterns
1. Fetch a user by `id` (primary key lookup).
2. Add, fetch or subtract from a user's `balance`.
3. Checking a user's `role` for access permissions.
4. Checking if a user's `password_hash` matches against a raw string for login.
5. Users are created once during registration and are re-accessed when logging in.

## ⚙️Operational Notes
None.