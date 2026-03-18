<h1 align="center">Quantum Mart Backend API</h1>

<p align="center">
    <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot"/>
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin"/>
    <img src="https://img.shields.io/badge/PostgreSQL-336791?logo=postgresql&logoColor=white" alt="PostgreSQL"/>
</p>

<p align="center">
    A Spring Boot API service that powers the core features of Quantum Mart.
</p>

## Table of Contents
- [Architecture Summary](#architecture-summary)
- [API Overview](#api-overview)
- [Developer Setup](#developer-setup)
- [Testing](#testing)
- [Roadmap](#roadmap)

## Architecture Summary
The backend for Quantum Mart uses a layered architecture: Controllers handle HTTP requests, services handle business logic,
repositories manage database access, and PostgreSQL holds the persistence layer. This structure keeps concerns separated and
organized. Core domains such as users, product listings and orders, are modeled explicitly and [documented](https://github.com/MattCumbo99/qmart/tree/master/docs/database/tables) 
accordingly. State machines enforce valid transitions, like setting the status on an order item. 

JWT-based authentication is also used to enhance system integrity and security. Some controller methods require a valid token 
before it is allowed to execute, and some require a certain level of privilege based on a pre-defined role hierarchy.

## API Overview
- `/api/auth` - Login
- `/api/users` - Accounts, profiles
- `/api/item-listings` - Product listings
- `/api/orders` - Purchased orders
- `/api/order-items` - Itemized purchases in an order
- `/api/cart-items` - User cart

## Getting Started
You need Java and a locally running [PostgreSQL](https://www.postgresql.org/) database. The application will not start without it.

1. [Download](https://github.com/MattCumbo99/quantum-mart-services/releases) the latest release build.
2. Set the following environment variables:
   ```
   DB_URL=<db url> (ex: jdbc:postgresql://localhost:5432/qmartdb)
   DB_USERNAME=<db username> (ex: postgres)
   DB_PASSWORD=<db password> (ex: admin)
   SPRING_PROFILES_ACTIVE=dev
    ```
3. Run the jar file.

## Developer Setup
Before installation, ensure you have [PostgreSQL](https://www.postgresql.org/download/) downloaded on your machine. It is recommended you install [pgadmin](https://www.pgadmin.org/download/) 
to manage the local database.

1. Clone this repository:
    ```
    https://github.com/MattCumbo99/qmart.git
    ```
2. Create a run configuration for the application with the following environment variables:
    ```
    DB_URL=<db url> (ex: jdbc:postgresql://localhost:5432/qmartdb)
    DB_USERNAME=<db username> (ex: postgres)
    DB_PASSWORD=<db password> (ex: admin)
    SPRING_PROFILES_ACTIVE=dev
    ```
   Set the main class as `com.mattrition.qmart.QmartApplication`. You need Java 22 SDK to build and run.

3. Run the application. If there are no exceptions, then you have successfully set up the backend services. As you start using 
    the frontend application, incoming requests will be logged to the backend.

If you need troubleshooting help, feel free to reach out to [@MattCumbo99](https://github.com/MattCumbo99).

## Testing
All existing and newly implemented changes to the API should be tested using mock calls. You can run all existing tests 
using `gradle test`. Note that you will not be able to merge in your changes if any test fails.

## Roadmap
You can see what is being planned / developed on the current project board: https://github.com/users/MattCumbo99/projects/6/views/1