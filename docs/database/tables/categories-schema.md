# Categories Table

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
- Table name: `categories`

| Column Name | Datatype                   | Nullable  | Default             | Description                           |
|-------------|----------------------------|-----------|---------------------|---------------------------------------|
| id          | PK `UUID`                  | No        | `gen_random_uuid()` | Primary identifier.                   |
| name        | `TEXT`                     | No        |                     | Display name.                         |
| slug        | `TEXT`                     | No        |                     | Unique category identifier for links. |
| is_active   | `BOOLEAN`                  | No        | true                | If the category can be queried.       |
| created_at  | `TIMESTAMP WITH TIME ZONE` | No        | `now()`             |                                       |
| updated_at  | `TIMESTAMP WITH TIME ZONE` | No        | `now()`             |                                       |

## 🎯Purpose
Standalone entities meant for group identifiers on item listings.

## ⏱️Lifecycle
### ➕Row Creation
System admins and up can create a new category.

By default, a single category named "Uncategorized" is created to allow existing item listings 
to stay relevant.

### 🔄Row Updates
Admins can change the `name`, `slug`, or set `is_active` at any time.

### 🗑️Row Deletion
All entities are permanent. Setting `is_active` to false is hides the category.

## 📌Important Columns
- **slug** - Primary way of determining associations.
- **is_active** - Determines visibility of the category.

## 🤝Relationships
- Has many: `item_listings` - Categories identify a subset of listings.

## 🔒Invariants
- `slug` must be unique and abide by this regex: `^[a-z0-9-]+$`

## 🔍Access Patterns
- GET all categories where `is_active` is true
- Admins: GET all categories

## ⚙️Operational Notes
None.
