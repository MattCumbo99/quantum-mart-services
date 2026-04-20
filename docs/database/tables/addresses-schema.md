# Addresses Table

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
- Table name: `addresses`

| Column Name   | Datatype                   | Nullable | Default | Description                                  |
|---------------|----------------------------|----------|---------|----------------------------------------------|
| id            | PK `UUID`                  | No       |         | Entity identifier.                           |
| user_id       | FK `UUID`                  | No       |         | Linked user.                                 |
| is_primary    | `BOOLEAN`                  | No       | false   | Marks the address as the user's primary.     |
| first_name    | `TEXT`                     | No       |         |                                              |
| last_name     | `TEXT`                     | No       |         |                                              |
| address_line1 | `TEXT`                     | No       |         | First line of address (street, number, etc). |
| address_line2 | `TEXT`                     | Yes      | `null`  | Second line of address (unit number).        |
| city          | `TEXT`                     | No       |         |                                              |
| zip           | `TEXT`                     | No       |         | ZIP code.                                    |
| phone         | `TEXT`                     | No       |         | Phone number.                                |
| created_at    | `TIMESTAMP WITH TIME ZONE` | No       | `now()` | Date of creation for the entity.             |

## 🎯Purpose
Stores user address information for a quicker checkout experience.

## ⏱️Lifecycle
### ➕Row Creation
Users can create addresses in their user settings, or they can create one at checkout.

### 🔄Row Updates
- `is_primary` changes if the user's current primary address is deleted.

### 🗑️Row Deletion
Users can manually delete a saved address at any time. If a user entity is deleted, all associated addresses are also
deleted.

## 📌Important Columns
- **id** — Used to retrieve address information.
- **is_primary** — The user's default address. Automatically selected at checkout.

## 🤝Relationships
- Belongs to: `users` - A single user can have multiple addresses.

## 🔒Invariants
- Only one address per user can be set as the primary address.
- Should the current primary address be deleted, the most recently added one will be set as the primary.

## 🔍Access Patterns
- Retrieve all addresses associated with a user.
- Retrieve the user's primary address.

## ⚙️Operational Notes
None.