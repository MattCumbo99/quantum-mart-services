# Reviews Table

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
- Table name: `reviews`

| Column Name  | Datatype                   | Nullable | Default             | Description                        |
|--------------|----------------------------|----------|---------------------|------------------------------------|
| id           | PK `UUID`                  | No       | `gen_random_uuid()` | Primary identifier                 |
| user_id      | FK `UUID`                  | No       |                     | ID of user who created the review. |
| listing_id   | FK `UUID`                  | No       |                     | ID of listing the review is on.    |
| body         | `TEXT`                     | No       |                     | Review text body.                  |
| score        | `INTEGER`                  | No       |                     | Review score.                      |
| is_edited    | `BOOLEAN`                  | No       | false               | If the review was edited.          |
| created_at   | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             |                                    |
| updated_at   | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             |                                    |

## 🎯Purpose
Holds reviews on an item listing.

## ⏱️Lifecycle
### ➕Row Creation
Users can create a single review on an item listing if the following conditions are met:
1. The user must not own the listing
2. The user must not have left a review already

### 🔄Row Updates
Only the `body` and `score` are updated by users. `is_edited` is updated by the system if the patch call is used.

### 🗑️Row Deletion
Users can delete their reviews at any time. Entities are not permanent, and will automatically cascade deletion if the 
user who authored the review is deleted.

## 📌Important Columns
- **id** — Primary key, used for deletion and editing.
- **body** — Description of the review and why the user left the score.
- **score** — Score the user gives the item.

## 🤝Relationships
- Belongs to: Users - A user can leave one review per item listing.
- Belongs to: Item Listings - An item listing has many reviews.

## 🔒Invariants
- `score` must be between 1 and 5

## 🔍Access Patterns
- Get reviews by listing ID
- Get reviews by user ID

## ⚙️Operational Notes
Operations performed on this table also affect the item listing table.
