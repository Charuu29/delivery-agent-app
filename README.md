# Simple Delivery Agent Application

A small full-stack web app that simulates a delivery agent's basic workflow. Users can create shipments, view them in a table, search and filter them, and update the delivery status.

## Tech stack

| Layer    | Technology                                                                 |
|----------|----------------------------------------------------------------------------|
| Backend  | Java 17, Spring Boot 4.1, Spring Web, Spring Data JPA / Hibernate, Maven   |
| Frontend | React (JavaScript, Vite), Axios, plain CSS                                 |
| Database | PostgreSQL                                                                 |

## Project structure

```
delivery-agent/
├── delivery-agent/   # Spring Boot backend
│   └── src/main/java/com/example/deliveryagent/
│       ├── controller/   # REST endpoints
│       ├── service/      # business logic
│       ├── repository/   # Spring Data JPA
│       ├── model/        # Shipment entity, ShipmentStatus enum
│       ├── dto/          # request / response objects
│       ├── exception/    # custom exceptions + global handler
│       └── config/       # CORS and seed data
└── frontend/         # React app
    └── src/
        ├── api.js        # all Axios calls
        ├── App.jsx
        └── components/   # form, table, status dialog
```

## Prerequisites

- JDK 17
- Node.js (LTS) and npm
- PostgreSQL (running on port 5432)

## Setup and run

### 1. Create the database

Using pgAdmin, or psql:

```sql
CREATE DATABASE delivery_db;
```

The `shipments` table is created automatically by Hibernate on the first run (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Run the backend

Database credentials are read from environment variables, so no password is stored in the source code.

| Variable      | Default    | Description       |
|---------------|------------|-------------------|
| `DB_USERNAME` | `postgres` | Database user     |
| `DB_PASSWORD` | none       | Database password |

Windows PowerShell:

```powershell
cd delivery-agent
$env:DB_PASSWORD="your_password"
.\mvnw spring-boot:run
```

macOS / Linux:

```bash
cd delivery-agent
export DB_PASSWORD="your_password"
./mvnw spring-boot:run
```

The API runs at `http://localhost:8080`.

### 3. Run the frontend

In a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

By default the frontend calls `http://localhost:8080/api`. To use a different backend URL, set `VITE_API_URL` in a `.env` file inside `frontend/`.

## Seed data

On the first start, if the table is empty, five sample shipments (TRK1001 to TRK1005) are inserted automatically by `DataSeeder`, with a mix of statuses.

## Status flow

```
ASSIGNED -> OUT_FOR_DELIVERY -> DELIVERED
                             -> DELIVERY_FAILED (failure reason required)
```

- Unknown status values are rejected with `400 Bad Request`.
- DELIVERED and DELIVERY_FAILED are final statuses.

## Validation rules

| Field              | Rule                                    |
|--------------------|-----------------------------------------|
| trackingNumber     | required, unique, max 50                |
| customerName       | required, max 100                       |
| customerMobile     | required, exactly 10 digits             |
| deliveryAddress    | required, max 500                       |
| destinationPincode | required, exactly 6 digits              |
| failureReason      | required when status is DELIVERY_FAILED |

## API endpoints

| Method | URL                          | Description                                  |
|--------|------------------------------|----------------------------------------------|
| POST   | `/api/shipments`             | Create a shipment                            |
| GET    | `/api/shipments`             | List shipments (optional `status`, `search`) |
| GET    | `/api/shipments/{id}`        | Get one shipment                             |
| PATCH  | `/api/shipments/{id}/status` | Update status and failure reason             |

Examples: `GET /api/shipments?status=DELIVERED` and `GET /api/shipments?search=ravi`

### Create a shipment

`POST /api/shipments`

Request:

```json
{
  "trackingNumber": "TRK2001",
  "customerName": "Test User",
  "customerMobile": "9876500000",
  "deliveryAddress": "1 Test Road, Vellore",
  "destinationPincode": "632001"
}
```

Response `201 Created`:

```json
{
  "id": 6,
  "trackingNumber": "TRK2001",
  "customerName": "Test User",
  "customerMobile": "9876500000",
  "deliveryAddress": "1 Test Road, Vellore",
  "destinationPincode": "632001",
  "status": "ASSIGNED",
  "failureReason": null,
  "createdAt": "2026-10-28T16:17:37.977",
  "updatedAt": "2026-10-28T16:17:37.977"
}
```

### Update status

`PATCH /api/shipments/1/status`

```json
{ "status": "OUT_FOR_DELIVERY" }
```

Response `200 OK`: the updated shipment with `"status": "OUT_FOR_DELIVERY"`.

Failed delivery:

```json
{ "status": "DELIVERY_FAILED", "failureReason": "Customer not available" }
```

### Error responses

Validation error, `400 Bad Request`:

```json
{
  "timestamp": "2026-10-28T16:20:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": {
    "customerName": "Customer name is required",
    "customerMobile": "Mobile number must be exactly 10 digits"
  }
}
```

| Situation                                                                     | HTTP status |
|-------------------------------------------------------------------------------|-------------|
| Created successfully                                                          | 201         |
| Read or updated successfully                                                  | 200         |
| Invalid input, unknown status, missing failure reason, invalid status change  | 400         |
| Shipment not found                                                            | 404         |
| Duplicate tracking number                                                     | 409         |

## Database schema

Table `shipments`:

| Column              | Type         | Notes             |
|---------------------|--------------|-------------------|
| id                  | bigint       | primary key, auto |
| tracking_number     | varchar(50)  | unique, not null  |
| customer_name       | varchar(100) | not null          |
| customer_mobile     | varchar(15)  | not null          |
| delivery_address    | varchar(500) | not null          |
| destination_pincode | varchar(6)   | not null          |
| status              | varchar(30)  | not null          |
| failure_reason      | varchar(255) | nullable          |
| created_at          | timestamp    | not null          |
| updated_at          | timestamp    | not null          |

## Frontend features

- Shipment creation form with field-level error messages
- Shipment table with search (debounced) and status filter
- Status update dialog (only valid next statuses are offered; reason required for failed delivery)
- Loading, empty, error (with retry), and success states
- Responsive layout
