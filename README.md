# ReUseMe

A web application that allows users to donate unwanted items.

ReUseMe connects people who want to give items away with people and charities who can use them. Features include item listings and a marketplace, charity registration and stores, collection scheduling and tracking, messaging and live chat, reviews, notifications, badges, user reports, a CO₂ savings calculator, and an admin dashboard.

## Contributors

- Carlos Dorado (@CarlosDoradoPedraza)
- Ali Mofidi ([@AliMofidi21](https://github.com/AliMofidi21))
- Farhan Tanvir Faruque ([@FTFaruque](https://github.com/FTFaruque))
- [@msharifA](https://github.com/msharifA)
- [@Yahye-Ashkir](https://github.com/Yahye-Ashkir)

## Tech stack

| Part | Location | Technologies |
| --- | --- | --- |
| Frontend | [`reuseme-app/`](reuseme-app/) | React, Vite, Material UI, Axios, STOMP/SockJS |
| Backend | [`reusemedb/`](reusemedb/) | Java 17, Spring Boot 3, Spring Security + JWT, Spring Data JPA, WebSocket, SendGrid |
| Database | — | H2 (in-memory) |

## Prerequisites

- [JDK 17](https://adoptium.net/) or newer
- [Node.js](https://nodejs.org/) (LTS) and npm

Maven doesn't need to be installed; the backend includes the Maven wrapper (`mvnw`).

## Getting started

### 1. Configure the backend

The backend reads its secrets from environment variables. For local development, put them in a file named `reusemedb/.env`, which git ignores. Never commit it.

Create `reusemedb/.env` with the following contents and fill in your own values:

```properties
# Required. Base64-encoded, at least 256 bits.
# Generate with: openssl rand -base64 64
JWT_SECRET=
# Token lifetime in milliseconds (default: 24 hours)
JWT_EXPIRATION=86400000

# Admin dashboard login. Admin login is disabled while either is blank.
ADMIN_EMAIL=
ADMIN_PASSWORD=

# SendGrid. Emails are skipped (and logged) while either is blank.
# The sender address must be verified in your SendGrid account.
SENDGRID_API_KEY=
SENDGRID_FROM_EMAIL=

# Optional Spring Security default user (a random password is generated if blank)
SECURITY_USER_NAME=user
SECURITY_USER_PASSWORD=

# Optional. The H2 database is in-memory, so this can stay blank.
DB_PASSWORD=
```

| Variable | Required | Purpose |
| --- | --- | --- |
| `JWT_SECRET` | Yes | Signs login tokens. The backend won't start without it. |
| `JWT_EXPIRATION` | No | Token lifetime in milliseconds. Defaults to 24 hours. |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | For admin access | Credentials for the admin dashboard. |
| `SENDGRID_API_KEY`, `SENDGRID_FROM_EMAIL` | For emails | Sends emails such as password resets. Without them, the app runs but logs emails instead of sending them. |
| `SECURITY_USER_NAME`, `SECURITY_USER_PASSWORD` | No | Spring Security's default user. |
| `DB_PASSWORD` | No | H2 database password. |

You can set the same variables in your shell or IDE run configuration instead of using a `.env` file. The backend looks for `.env` in the directory it is started from, and also in `reusemedb/` when started from the repository root.

### 2. Run the backend

```bash
cd reusemedb
./mvnw spring-boot:run        # on Windows: mvnw.cmd spring-boot:run
```

You can also run `ReusemedbApplication.java` directly from your IDE.

The API runs at <http://localhost:8080>. The database is in-memory, so data is reset each time the backend restarts. While it is running, you can inspect the database in the H2 console at <http://localhost:8080/h2-console>.

### 3. Run the frontend

In a second terminal:

```bash
cd reuseme-app
npm install
npm run dev
```

Open <http://localhost:5173>. The Vite dev server forwards `/api` and `/ws-chat` requests to the backend on port 8080, so keep the backend running.

## Frontend scripts

Run these from `reuseme-app/`:

| Command | Description |
| --- | --- |
| `npm run dev` | Start the development server |
| `npm run build` | Build for production into `dist/` |
| `npm run preview` | Preview the production build |
| `npm run lint` | Run ESLint |

## Running backend tests

```bash
cd reusemedb
./mvnw test
```

## Known limitations

This is a university group project and is not production-ready. In particular, **authorisation is incomplete**:

- Only `/api/messages/**` and `/api/admin/**` require a valid JWT. All other `/api/` endpoints are open to unauthenticated requests (`anyRequest().permitAll()` in `SecurityConfig`).
- The frontend stores the logged-in user's ID in `localStorage` and sends it with requests. Endpoints that act on a user ID taken from the request, rather than from the verified token, can be called on behalf of any user.
- Admin login checks a single configured email and password, and returns a fixed placeholder token rather than a signed JWT. There are no user roles.

Don't deploy this project publicly without first requiring authentication by default, deriving the current user from the verified JWT, and adding role-based access control for admin features.
