# Notifications Table

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
- Table name: `notifications`

| Column Name  | Datatype                   | Nullable | Default | Description                                       |
|--------------|----------------------------|----------|---------|---------------------------------------------------|
| id           | PK `UUID`                  | No       |         | Primary identifier.                               |
| user_id      | FK `UUID`                  | No       |         | User ID association.                              |
| message      | `TEXT`                     | No       |         |                                                   |
| route        | `TEXT`                     | No       |         | URL to navigate when the notification is clicked. |
| read_at      | `TIMESTAMP WITH TIME ZONE` | Yes      |         | When the notification was read.                   |
| created_at   | `TIMESTAMP WITH TIME ZONE` | No       | `now()` |                                                   |
| deleted_at   | `TIMESTAMP WITH TIME ZONE` | Yes      |         | When the user hid the notification.               |

## 🎯Purpose
Stores messages notifying users of a flow change related to orders, purchases, sales, anything that requires attention.

## ⏱️Lifecycle
### ➕Row Creation
Only the backend services can create notifications. A notification is created when:
1. Order item is marked as complete
2. User purchases an item listing
3. Seller ships an order item

### 🔄Row Updates
- `read_at` changes when the user marks a notification as read.
- `deleted_at` is changed when the user wants to remove the notification.

### 🗑️Row Deletion
All notifications are permanently stored in the database. No rows are hard-deleted UNLESS the associated user entity is
also deleted.

## 📌Important Columns
- **user_id** for getting associated notifications
- **route** used for page navigation

## 🤝Relationships
- Belongs to: `users` - A notification belongs to a user.

## 🔒Invariants
Nothing to note.

## 🔍Access Patterns
- Retrieve user notifications that weren't deleted by the user

## ⚙️Operational Notes
Nothing to note.