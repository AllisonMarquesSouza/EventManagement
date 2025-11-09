# Event Management API

This is a robust REST API built with Spring Boot for managing events, user registrations, and authentication. It features a secure, stateless architecture using JSON Web Tokens (JWT) and a role-based access control system.

The project is built with a clean, service-oriented architecture, using Spring Data JPA for database interaction, Flyway for database migrations, and a centralized exception handling mechanism.

## Core Features ✨

  * **Authentication:** Secure user registration and login endpoints.
  * **JWT Security:** Stateless, token-based security using JWT (com.auth0). A custom `SecurityFilter` validates the token on every request.
  * **Role-Based Access Control (RBAC):**
      * **PARTICIPANT** (default role): Can browse events and register themselves.
      * **ADMIN:** Can do everything a participant can, *plus* create, update, and delete events.
  * **Event Management:** Full CRUD (Create, Read, Update, Delete) functionality for events.
  * **Event Filtering & Search:** Find events by date, location, title, or availability (spots open).
  * **Registration System:** Users can register for events. Business logic prevents overbooking (checking `maxParticipants` vs. `registeredParticipants`) and duplicate registrations.
  * **Database Migrations:** Uses Flyway to manage database schema evolution, ensuring the schema is consistent across all environments.
  * **Centralized Exception Handling:** A global `@RestControllerAdvice` provides consistent, clean JSON error responses for common issues (e.g., 404 Not Found, 409 Conflict, 401 Unauthorized).

## Tech Stack 🛠️

  * **Framework:** Spring Boot 3.5.5
  * **Language:** Java 21
  * **Security:** Spring Security, Auth0 JWT (java-jwt)
  * **Database:** Spring Data JPA, PostgreSQL (Driver)
  * **Migrations:** Flyway
  * **Utilities:** Lombok, Spring Boot Validation

## API Endpoints 🚀

Here is a summary of the available endpoints.

### 1\. Authentication (`/auth`)

These endpoints are public.

  * `POST /auth/register`
      * **Description:** Registers a new user. The default role is `PARTICIPANT`.
      * **Body:** `RegisterDto`
        ```json
        {
          "username": "new_user",
          "password": "A_Very_Strong_Password_123",
          "email": "user@example.com"
        }
        ```
  * `POST /auth/login`
      * **Description:** Authenticates a user and returns a JWT token.
      * **Body:** `AuthenticationDto`
        ```json
        {
          "username": "new_user",
          "password": "A_Very_Strong_Password_123"
        }
        ```
      * **Success Response:** `TokenDto`
        ```json
        {
          "token": "eyJh...[jwt_token]...c4o"
        }
        ```

-----

### 2\. Events (`/event`)

Access to these endpoints requires a valid JWT. Write operations (POST, PATCH, DELETE) are restricted to **ADMINS**.

  * `GET /event`
      * **Description:** Lists all events.
  * `GET /event/{id}`
      * **Description:** Gets a single event by its UUID.
  * `GET /event/date/{date}`
      * **Description:** Finds events on a specific date (format: `YYYY-MM-DD`).
  * `GET /event/available`
      * **Description:** Lists all events that are not full (`registeredParticipants < maxParticipants`).
  * `GET /event/filter`
      * **Description:** Searches for events by title and/or location.
      * **Query Params:** `?title=Example&location=City`
  * `POST /event` **(ADMIN ONLY)**
      * **Description:** Creates a new event.
      * **Body:** `EventCreateDto`
        ```json
        {
          "title": "Spring Boot Conference",
          "location": "Main Auditorium",
          "date": "2025-11-20 10:00",
          "maxParticipants": 150
        }
        ```
  * `PATCH /event/title/{id}` **(ADMIN ONLY)**
      * **Description:** Updates an event's title.
  * `PATCH /event/location/{id}` **(ADMIN ONLY)**
      * **Description:** Updates an event's location.
  * `PATCH /event/date/{id}` **(ADMIN ONLY)**
      * **Description:** Updates an event's date.
  * `PATCH /event/participants/{id}` **(ADMIN ONLY)**
      * **Description:** Updates an event's maximum participant count.
  * `DELETE /event/{id}` **(ADMIN ONLY)**
      * **Description:** Deletes an event.

-----

### 3\. Registrations (`/registration`)

Access to these endpoints requires a valid JWT.

  * `POST /registration`
      * **Description:** Registers the authenticated user for an event. The service logic prevents registration if the event is full or if the user is already registered.
      * **Body:** `RegistrationCreateDto`
        ```json
        {
          "userId": "user-uuid-here",
          "eventId": "event-uuid-here"
        }
        ```
  * `GET /registration/{id}`
      * **Description:** Gets a single registration record by its UUID.
  * `GET /registration/user/{id}`
      * **Description:** Finds all registrations for a specific user.
  * `GET /registration/event/{id}`
      * **Description:** Finds all registrations for a specific event.
  * `DELETE /registration/{id}`
      * **Description:** Deletes a registration by its unique ID (cancels a booking).
  * `DELETE /registration/user/{userId}`
      * **Description:** Deletes all registrations associated with a user.
  * `DELETE /registration/user/{userId}/event/{eventId}`
      * **Description:** Deletes a specific user's registration for a specific event.

## Database Schema

The database is structured into three main tables, managed by Flyway migrations.

  * **`users`**
      * `id` (PK)
      * `username`
      * `email`
      * `password` (encrypted)
      * `role` (ADMIN or PARTICIPANT)
  * **`event`**
      * `id` (PK)
      * `title`
      * `location`
      * `date`
      * `max_participants`
      * `registered_participants` (This count is updated by the `RegistrationService`)
  * **`registration`**
      * `id` (PK)
      * `user_id` (FK to `users.id`)
      * `event_id` (FK to `event.id`)
      * `created_at`

This creates a many-to-many relationship between `users` and `events` via the `registration` table.

## Security Model

Security is configured in `SecurityConfiguration.java`.

  * **Stateless:** The session policy is `SessionCreationPolicy.STATELESS`.
  * **Filter Chain:** A custom `SecurityFilter` runs before the standard authentication filter, intercepting all requests.
    1.  It looks for an `Authorization` header.
    2.  It recovers the `Bearer <token>`.
    3.  It uses `TokenService` to validate the JWT.
    4.  If valid, it retrieves the `UserDetails` from the `UserRepository` and sets the `SecurityContextHolder`, authenticating the user for the request.
  * **Endpoint Authorization:**
      * `permitAll()`: `/auth/login` and `/auth/register`.
      * `hasRole("ADMIN")`: All `POST`, `PATCH`, and `DELETE` methods on `/event/**`.
      * `authenticated()`: All other requests not listed above.

## Exception Handling

The `GlobalExceptionHandler` intercepts thrown exceptions and returns a standardized `ErrorResponse` object.

  * `EntityNotFoundException` ➡️ **HTTP 404 Not Found**
  * `EntityAlreadyExistsException` ➡️ **HTTP 409 Conflict** (e.g., username taken, user already registered for event)
  * `BadRequestException` ➡️ **HTTP 400 Bad Request**
  * `AuthenticationException` ➡️ **HTTP 401 Unauthorized** (e.g., bad credentials)
  * `AccessDeniedException` (from Spring) ➡️ **HTTP 403 Forbidden** (e.g., a `PARTICIPANT` trying to delete an event)
  * `Exception` (Generic) ➡️ **HTTP 500 Internal Server Error**

## How to Run 🏃

1.  **Clone the repository:**
    ```bash
    git clone [your-repo-url]
    cd eventmanagement
    ```
2.  **Set Environment Variables:**
    This project uses environment variables to configure the database and security. You can set these in your IDE's run configuration or in your operating system.
    ```properties
    # application.properties
    PG_HOST=localhost
    PG_PORT=5432
    PG_DATABASE=eventmanagement
    USERNAME=postgres
    PASSWORD=your_db_password
    SECRET_JWT=your_super_secret_jwt_key_here
    ```
3.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start, and Flyway will automatically run the database migrations (`V1`, `V2`, `V3`) to set up your tables.
