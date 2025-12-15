## econoMe – Personal Expense Manager

**econoMe** is a full‑stack personal expense manager that lets users register, categorize, and analyze their income and expenses.  
It is built with **Spring Boot 3** (Java 17) on the backend and **Vue 3** on the frontend, with JWT‑based authentication and role management.

---

## Tech stack

- **Backend**: Spring Boot 3, Spring Data JPA, Spring Security, JWT, MySQL / H2
- **Frontend**: Vue 3, Vue Router, Vuex, Axios, Chart.js
- **Build tools**: Maven, npm

---

## Project structure

At the root of the repository:

- **`gestor-gastos-back/`** – Spring Boot backend (REST API, security, persistence)
- **`gestor-gastos-front/`** – Vue 3 frontend (SPA that consumes the backend API)

If you prefer shorter names, you can conceptually think of:

- `gestor-gastos-back` → **backend**
- `gestor-gastos-front` → **frontend**

Inside the backend (`gestor-gastos-back`):

- **`src/main/java/com/econoMe/gestorgastosback`**
  - `controller/` – REST controllers (auth, users, accounting, operations)
  - `service/` – Business logic and orchestration
  - `repository/` – Spring Data JPA repositories
  - `model/` – JPA entities (users, roles, operations, accounting)
  - `dto/` – Data transfer objects used by the API
  - `security/`, `jwt/`, `aspect/` – Security configuration, JWT handling, and cross‑cutting concerns
- **`src/main/resources/application.properties`** – Main backend configuration (DB, ports, security properties)

Inside the frontend (`gestor-gastos-front`):

- **`src/api/`** – Small wrappers around Axios for each backend area (`accountingAPI`, `authAPI`, etc.)
- **`src/components/`** – Reusable Vue components (tables, modals, sidebars, forms, etc.)
- **`src/views/`** – Page‑level views (home, register, modify password, etc.)
- **`src/stores/`** – Vuex stores (global state, accounting state)
- **`src/utils/`** – Utilities (Axios instance, charts, global helpers, pagination, toasts)
- **`src/assets/`** – Static assets (icons, images, global styles)

---

## Getting started

### Prerequisites

- **Java 17**
- **Maven 3+** (or the included wrapper `mvnw` / `mvnw.cmd`)
- **Node.js** (LTS recommended) and **npm**
- **MySQL** (or use the in‑memory H2 database for development)

---

## Backend – `gestor-gastos-back`

### 1. Configure the database

Edit `gestor-gastos-back/src/main/resources/application.properties` and set at least:

- **JDBC URL**
- **Username / password**
- **Dialect** (MySQL or H2)

If you are using MySQL, make sure the database exists and the connection details match.

### 2. Run the backend

From the project root:

```bash
cd gestor-gastos-back

# Using Maven wrapper (recommended on Windows in this repo)
./mvnw spring-boot:run   # or mvnw.cmd spring-boot:run

# Or, if you have Maven installed globally
mvn spring-boot:run
```

By default, the backend is expected to run on **`http://localhost:8081`** and expose its REST API under `/api`.

You can run the backend tests with:

```bash
mvn test
```

---

## Frontend – `gestor-gastos-front`

### 1. Install dependencies

From the project root:

```bash
cd gestor-gastos-front
npm install
```

### 2. Configure the API URL (if needed)

The Axios instance is configured in `src/utils/axios.js`:

```js
baseURL: 'http://localhost:8081/api'
```

If you change the backend port or base path, update this value accordingly.

### 3. Run the frontend

```bash
npm run serve
```

The Vue dev server will usually run on **`http://localhost:8080`**.

### 4. Build for production

```bash
npm run build
```

### 5. Lint and fix files

```bash
npm run lint
```

---

## How the app works (high level)

- **Authentication & Authorization**
  - Users authenticate via the backend using JWT.
  - Spring Security and JWT filters protect API endpoints.
  - Roles and permissions are modeled and enforced on the backend.

- **Expense / Income management**
  - Users can create and manage **accountings** and **operations** (incomes/expenses).
  - The frontend uses Vuex to manage global state and Axios to talk to the backend.
  - Charts and dashboards use Chart.js to show evolution and distribution of spending/income.

---

## Documentation

The full written report for this project is available in the repository root as:

- **`TFG Jesus Leyva Fernandez.pdf`** – complete documentation of the system (requirements, design, implementation, and evaluation).

You can open it directly from your IDE or file explorer, or view/download it from the repository hosting service (e.g. GitHub) where it will appear alongside this `README.md`.
