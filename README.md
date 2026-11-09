# AutoSphere — Dealership Platform

AutoSphere is a full-stack dealership workflow demo built for the Isocode Software Engineering Intern take-home. It covers vehicle discovery, city-wise on-road pricing, test-drive and service requests, trade-in enquiries, sales leads, admin content, role management, and assisted sales workflows.

See [deployment.md](deployment.md) for the full local-run and hosted-deployment guide.

## Stack

- React 18, TypeScript, Vite, Tailwind CSS
- Java 21, Spring Boot 3.4, Spring Security, Spring Data JPA, Maven
- PostgreSQL 16 with Flyway migrations
- Redis/Valkey and Kafka in Docker Compose for supporting cache/async infrastructure
- Groq API for optional buyer summaries and reply drafts; deterministic fallback when no API key is configured
- Docker Compose for local supporting services

## Quick start (Windows)

Prerequisites: Java 21, Maven 3.9+, Node.js 20+, npm, Git, and Docker Desktop.

1. Open a terminal in the project root and start the supporting services:
   ```
   docker compose up -d
   docker compose ps
   ```
   Wait until PostgreSQL reports healthy.
2. Start the backend in terminal 1 (PowerShell):
   ```
   cd backend
   $env:DB_URL="jdbc:postgresql://localhost:5432/autosphere"
   $env:DB_USERNAME="autosphere"
   $env:DB_PASSWORD="autosphere_local"
   $env:JWT_SECRET="local-development-secret-change-this-to-at-least-32-characters"
   $env:GROQ_API_KEY=""
   mvn spring-boot:run
   ```
3. Start the frontend in terminal 2:
   ```
   cd frontend
   npm ci
   npm run dev
   ```
4. Open http://localhost:5173. API health: http://localhost:8080/api/health.

If port 5173 is busy, run `npm run dev -- --port 5174 --strictPort` and open http://localhost:5174.

A root `.env.example` is provided. The values above are local-development placeholders only; never commit real secrets. `npm ci` installs platform-specific native packages, so do not reuse a `node_modules` folder copied from another operating system. To stop services: `docker compose down` (avoid `-v` unless you want to delete the local database).

## Demo accounts

| Role | Email |
|---|---|
| Buyer | `buyer@autosphere.demo` |
| Sales consultant | `sales@autosphere.demo` |
| Used-car manager | `usedcars@autosphere.demo` |
| Admin | `admin@autosphere.demo` |

Demo passwords are provided separately in the evaluator instructions. All records and credentials are synthetic. Change all seeded credentials and secrets before any public deployment.

## Implemented workflows

### Buyer

- Browse and filter new and certified pre-owned inventory.
- Open a vehicle-specific detail page with specifications, colours, used-car owner count, masked registration and inspection notes.
- Calculate city-wise on-road prices from stored admin price components (ex-showroom + road tax + insurance + accessories), with a validity date and admin note. Prices come from persisted admin data, never from AI.
- View indicative EMI based on principal, down payment, annual rate and tenure.
- Request a test drive by showroom, date and vehicle/demo car; available slots are checked against existing bookings and duplicate vehicle slots are rejected.
- Request service by workshop, date and slot. The demo enforces a capacity of three requests per workshop/time slot.
- Submit a trade-in valuation request.
- Provide consent on personal-data forms and submit an opt-out request.

### Sales / lead desk

- Lead inbox with status updates, assigned consultant, showroom, consent, acknowledgement and age/SLA display.
- Automatic assignment rules based on showroom and used-car model category.
- CSV import endpoint for legacy lead exports (stand-in for a DMS exchange). Expected columns: `name,email,phone,vehicleModel,message,showroom`. This lightweight importer expects unquoted comma-separated fields.
- AI-assisted buyer summary and editable first-reply draft. Drafts are never sent automatically, and the UI currently has no send action.
- Opt-out status is recorded on matching lead records.

### Used-car manager

- Separate `USED_CAR_MANAGER` role.
- Comparable-sales price band endpoint restricted to used-car manager and admin roles (enforced in the backend).
- Suggestions use seeded synthetic historical sales matched by model, nearby year and kilometre range. They are not a valuation guarantee; the manager sets the final price.

### Admin

- Add vehicle stock with used-car metadata and inspection notes.
- Update inventory status.
- Configure per-vehicle, per-city price components.
- Create offers and banners through the CMS API; active content is shown on the home page.
- View users and change roles.

## Important API routes

- `POST /api/auth/login`
- `GET /api/vehicles`, `GET /api/vehicles/{id}`
- `GET /api/pricing/{vehicleId}?city=Lucknow`
- `GET /api/bookings/availability?vehicleId=1&date=YYYY-MM-DD`, `POST /api/bookings`
- `POST /api/service/bookings`, `POST /api/trade-in`
- `GET /api/emi?principal=1000000&annualRate=9&months=60`
- `POST /api/leads`, `GET /api/leads`, `PATCH /api/leads/{id}/status`
- `GET /api/leads/{id}/summary`, `POST /api/leads/{id}/ai-draft`, `POST /api/leads/import-csv`
- `POST /api/privacy/opt-out`
- `GET /api/used-cars/{id}/price-suggestion` (used-car manager/admin only)
- `POST /api/admin/vehicles`, `PUT /api/admin/pricing/{vehicleId}`
- `/api/admin/content`, `/api/admin/users`

## Tests and checks

Backend:

```
cd backend
mvn test
```

Frontend:

```
cd frontend
npm ci
npm test
npm run build
```

The source ZIP does not include `node_modules` or Maven `target` output. Run `npm ci` on the target OS.

## Deployment

A hosted setup is a static frontend host, a Java-capable backend host and managed PostgreSQL. Follow the checklist in [deployment.md](deployment.md): production secrets as host secrets, frontend API base URL pointing to the deployed backend, CORS for the frontend origin, migrations against the production database, and HTTPS on both sides.

- **Live frontend URL:** _add after deployment and verification_
- **Live backend URL:** _add after deployment and verification_

## Demo boundaries / production work

- WhatsApp/SMS acknowledgement is recorded as a SIMULATED template event; no external messaging provider is connected.
- Test-drive and service slots are database-backed demo workflows. Service capacity is a demo count check; production requires transactional locking/reservation expiry and actual bay/demo-car calendars.
- Lead SLA is displayed and assigned; no telephony integration automatically calls a consultant.
- CSV import is intentionally small and accepts unquoted comma-separated rows. Production import should use a robust CSV parser, validation report and duplicate detection.
- Historical used-car comparables are synthetic seed data. Do not use them as a real market valuation.
- Email/WhatsApp sending is not enabled. AI output is draft-only and must be reviewed by a human.
- No online payments or DMS integration are included. No real customer data should be entered.
- Replace sample Unsplash imagery with licensed dealership photography. Configure HTTPS, production secrets, rate limiting, audit logs, backups, refresh-token rotation, monitoring and privacy retention workflows before production use.
- This is an engineering summary, not a legal compliance certification under the Digital Personal Data Protection Act, 2023.
