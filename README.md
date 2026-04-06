# Finance Data Processing and Access Control Backend

A production-ready REST API backend for a **Finance Dashboard System** built with Java 17, Spring Boot 3.2, PostgreSQL, and pgvector. The system supports multi-role user management, financial record CRUD operations, dashboard analytics, and AI-powered semantic search.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Security | Spring Security + JWT |
| Database | PostgreSQL 15+ |
| Vector Search | pgvector |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven 3.8+ |
| Testing | JUnit 5 + Mockito |

---

## Live Demo

| | URL |
|-|-----|
| **Base URL** | https://finance-backend-2nfa.onrender.com |
| **Swagger UI** | https://finance-backend-2nfa.onrender.com/swagger-ui/index.html |
| **API Docs** | https://finance-backend-2nfa.onrender.com/v3/api-docs |

> **Note:** The app is hosted on Render free tier. It may take **30-60 seconds** to wake up on the first request. Please wait and retry if you get a timeout.

---

## How to Use Swagger UI

The Swagger UI allows you to test all APIs directly from the browser without Postman.

### Step 1: Open Swagger UI
```
https://finance-backend-2nfa.onrender.com/swagger-ui/index.html
```

### Step 2: Register a User
```
1. Click on "1. Authentication" section
2. Click POST /api/auth/register
3. Click "Try it out"
4. Paste this in the request body:
{
  "name": "Admin User",
  "email": "admin@finance.com",
  "password": "admin123",
  "role": "ADMIN"
}
5. Click "Execute"
6. You should see 201 Created response
```

### Step 3: Login and Get Token
```
1. Click POST /api/auth/login
2. Click "Try it out"
3. Paste:
{
  "email": "admin@finance.com",
  "password": "admin123"
}
4. Click "Execute"
5. Copy the token from the response:
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...."
  }
}
```

### Step 4: Authorize with Token
```
1. Click the "Authorize 🔒" button at the TOP RIGHT of the Swagger page
2. A popup will appear
3. In the "Value" field paste ONLY the token
   (Do NOT type "Bearer" - Swagger adds it automatically)
4. Click "Authorize"
5. Click "Close"
```

### Step 5: Now All APIs Work!
```
You are now authenticated as ADMIN.
All APIs will work exactly like Postman.
The 🔒 icon on each endpoint will show as locked.
```

---

## Quick Test Guide in Swagger

After authorizing, test in this order:

| Step | Action | Endpoint | Expected |
|------|--------|----------|---------|
| 1 | Register ADMIN | POST /api/auth/register | 201 |
| 2 | Login | POST /api/auth/login | 200 + token |
| 3 | Authorize | Click 🔒 Authorize button | Token set |
| 4 | Create Transaction | POST /api/transactions | 201 |
| 5 | Get Transactions | GET /api/transactions | 200 |
| 6 | Get Dashboard | GET /api/dashboard/summary | 200 |
| 7 | Register VIEWER | POST /api/auth/register (role: VIEWER) | 201 |
| 8 | Login as VIEWER | POST /api/auth/login | 200 + token |
| 9 | Authorize as VIEWER | Click 🔒 with viewer token | Token set |
| 10 | Try Transactions as VIEWER | GET /api/transactions | 403 Forbidden |

---

## Project Structure

```
finance-pgvector/
├── src/
│   ├── main/
│   │   ├── java/com/finance/
│   │   │   ├── FinanceApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   └── EmbeddingController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── TransactionService.java
│   │   │   │   ├── DashboardService.java
│   │   │   │   └── EmbeddingService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── TransactionRepository.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   └── Transaction.java
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── TransactionDTO.java
│   │   │   │   └── UserDTO.java
│   │   │   ├── enums/
│   │   │   │   ├── Role.java
│   │   │   │   ├── UserStatus.java
│   │   │   │   └── TransactionType.java
│   │   │   ├── security/
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── DuplicateResourceException.java
│   │   │       └── UnauthorizedException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql
│   └── test/
│       └── java/com/finance/
│           ├── TransactionServiceTest.java
│           └── DashboardServiceTest.java
├── .gitignore
├── pom.xml
└── README.md
```

---

## Prerequisites

Before running this project make sure you have:

- Java 17 installed
- PostgreSQL 15 or higher installed and running
- Maven 3.8 or higher installed
- Postman (for API testing)

---

## Database Setup

**Step 1: Create the database**
```sql
CREATE DATABASE financedb;
```

**Step 2: Connect to the database**
```sql
\c financedb
```

**Step 3: Enable pgvector extension (optional - only needed for semantic search)**
```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

> **Note on pgvector:** pgvector requires manual installation on Windows. Download from [github.com/pgvector/pgvector/releases](https://github.com/pgvector/pgvector/releases), copy the files to your PostgreSQL installation directory, and restart PostgreSQL. If pgvector is not installed, all core APIs work normally — only the semantic search endpoint will be unavailable.

---

## Configuration

Edit `src/main/resources/application.properties` and update your password:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/financedb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE
```

---

## How to Run Locally

```bash
# Clone the repository
git clone https://github.com/kushwaha-ashutosh/finance-backend.git
cd finance-backend

# Run the application
mvn spring-boot:run
```

Server starts at: **http://localhost:8080**

Swagger UI locally: **http://localhost:8080/swagger-ui/index.html**

---

## Run Tests

```bash
# Clean and run all unit tests
mvn clean test
```

Expected output:
```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Role Permissions

The system has three roles with different levels of access:

| Action | ADMIN | ANALYST | VIEWER |
|--------|-------|---------|--------|
| Register | ✅ | ✅ | ✅ |
| Login | ✅ | ✅ | ✅ |
| View all users | ✅ | ❌ | ❌ |
| Update user status | ✅ | ❌ | ❌ |
| Delete user | ✅ | ❌ | ❌ |
| Create transaction | ✅ | ❌ | ❌ |
| View transactions | ✅ | ✅ | ❌ |
| Update transaction | ✅ | ❌ | ❌ |
| Delete transaction | ✅ | ❌ | ❌ |
| View dashboard summary | ✅ | ✅ | ✅ |
| Semantic search | ✅ | ✅ | ❌ |
| Generate embeddings | ✅ | ❌ | ❌ |

---

## API Endpoints

### Authentication
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | /api/auth/register | No | Register a new user |
| POST | /api/auth/login | No | Login and receive JWT token |

### User Management (ADMIN only)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/users | Get all users |
| GET | /api/users/{id} | Get user by ID |
| PATCH | /api/users/{id}/status | Update user status (ACTIVE/INACTIVE) |
| DELETE | /api/users/{id} | Delete a user |

### Transactions
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/transactions | ADMIN | Create a new transaction |
| GET | /api/transactions | ADMIN, ANALYST | Get all transactions |
| GET | /api/transactions/{id} | ADMIN, ANALYST | Get transaction by ID |
| PUT | /api/transactions/{id} | ADMIN | Update a transaction |
| DELETE | /api/transactions/{id} | ADMIN | Soft delete a transaction |
| GET | /api/transactions/search/semantic | ADMIN, ANALYST | Semantic search using pgvector |

**Query Parameters for GET /api/transactions:**
- `?category=Salary` — Filter by category
- `?type=INCOME` or `?type=EXPENSE` — Filter by type (INCOME or EXPENSE)
- `?from=2024-01-01&to=2024-12-31` — Filter by date range

### Dashboard
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | /api/dashboard/summary | ADMIN, ANALYST, VIEWER | Get full dashboard summary |

### Embeddings (pgvector)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/embeddings/generate-all | ADMIN | Generate embeddings for all transactions |

---

## Sample API Requests

### 1. Register a User
```json
POST /api/auth/register
Content-Type: application/json

{
  "name": "Admin User",
  "email": "admin@finance.com",
  "password": "admin123",
  "role": "ADMIN"
}
```

Response:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "Admin User",
    "email": "admin@finance.com",
    "role": "ADMIN",
    "status": "ACTIVE"
  }
}
```

### 2. Login
```json
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@finance.com",
  "password": "admin123"
}
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### 3. Create a Transaction
```json
POST /api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "amount": 50000,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-04-01",
  "notes": "April monthly salary"
}
```

Response:
```json
{
  "success": true,
  "message": "Transaction created successfully",
  "data": {
    "id": 1,
    "amount": 50000.0,
    "type": "INCOME",
    "category": "Salary",
    "date": "2024-04-01",
    "notes": "April monthly salary",
    "isDeleted": false
  }
}
```

### 4. Get All Transactions (with filters)
```
GET /api/transactions?category=Salary
GET /api/transactions?type=INCOME
GET /api/transactions?from=2024-01-01&to=2024-12-31
Authorization: Bearer YOUR_TOKEN
```

### 5. Get Dashboard Summary
```
GET /api/dashboard/summary
Authorization: Bearer YOUR_TOKEN
```

Response:
```json
{
  "success": true,
  "message": "Dashboard summary retrieved successfully",
  "data": {
    "totalIncome": 60000.0,
    "totalExpense": 23000.0,
    "netBalance": 37000.0,
    "categoryTotals": {
      "Salary": 50000.0,
      "Freelance": 10000.0,
      "Rent": 15000.0,
      "Food": 8000.0
    },
    "monthlyTrends": {
      "2024-04": 83000.0
    },
    "recentActivity": []
  }
}
```

### 6. Semantic Search (pgvector)
```
GET /api/transactions/search/semantic?query=salary+payment&topK=5
Authorization: Bearer YOUR_TOKEN
```

---

## Error Responses

All errors follow a consistent format:

```json
{
  "success": false,
  "message": "Error description here",
  "errors": ["field-level error 1", "field-level error 2"],
  "timestamp": "2024-04-01T10:00:00"
}
```

| HTTP Status | Meaning |
|-------------|---------|
| 200 | Success |
| 201 | Created successfully |
| 400 | Bad request / Validation error |
| 401 | Unauthorized - invalid or missing token |
| 403 | Forbidden - insufficient role permissions |
| 404 | Resource not found |
| 409 | Conflict - duplicate entry |
| 500 | Internal server error |

---

## Key Design Decisions

**Soft Delete** — Transactions are never permanently deleted. An `is_deleted` flag is set to true, preserving data integrity and audit history.

**JWT Authentication** — Stateless authentication using JWT tokens that expire after 24 hours, making the API horizontally scalable.

**Role Based Access Control** — Implemented using Spring Security `@PreAuthorize` annotations at the controller level, ensuring consistent and centralized enforcement across all endpoints.

**Global Exception Handling** — A centralized `@RestControllerAdvice` handles all exceptions and returns consistent, meaningful error responses with correct HTTP status codes.

**Layered Architecture** — Strict separation between Controller, Service, and Repository layers ensuring clean, maintainable, and testable code.

**pgvector Semantic Search** — Each transaction stores a vector embedding enabling meaning-based search. Mock embeddings are used by default and can be replaced with real OpenAI embeddings by updating `EmbeddingService.java`.

**EAGER Loading** — Transaction's `createdBy` field uses EAGER fetch type to prevent Hibernate lazy loading issues during JSON serialization.

**Schema Initialization** — Uses `schema.sql` with `IF NOT EXISTS` for safe, idempotent table creation on every startup without data loss.

---

## Assumptions Made

1. Registration is open — any user can register with any role. In production this would be restricted so only admins can assign the ADMIN role.
2. JWT tokens expire after 24 hours.
3. Passwords are encrypted using BCrypt with strength 12.
4. Soft delete is used for transactions — deleted records are hidden from all API responses but retained in the database.
5. The `from` date must not be after the `to` date when filtering by date range.
6. VIEWER role can only access the dashboard summary — they cannot view raw transaction records.
7. Embedding dimension is set to 8 for mock embeddings. Change to 1536 for real OpenAI embeddings.
8. Embedding generation failures are non-blocking — a transaction is still saved even if embedding generation fails.

---

## pgvector Setup (Windows)

pgvector enables AI-powered semantic search on transactions. On Windows it requires manual installation:

1. Download from: https://github.com/pgvector/pgvector/releases
2. Copy `vector.dll` to `C:\Program Files\PostgreSQL\{version}\lib\`
3. Copy `vector.control` and `.sql` files to `C:\Program Files\PostgreSQL\{version}\share\extension\`
4. Restart PostgreSQL service: `net stop postgresql-x64-16` then `net start postgresql-x64-16`
5. Run in psql: `CREATE EXTENSION IF NOT EXISTS vector;`

All core APIs work fully without pgvector. Only the semantic search endpoint requires it.

---

## Test Results

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.finance.DashboardServiceTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0

Running com.finance.TransactionServiceTest
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0

Results:
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

---

## Author

Built as part of a backend engineering screening assessment focusing on API design, data modeling, business logic, and access control.
