# AutoSphere — Dealership Platform

AutoSphere is a full-stack dealership workflow demo built for the Isocode Software Engineering Intern take-home. It covers vehicle discovery, city-wise on-road pricing, test-drive and service requests, trade-in enquiries, sales leads, admin content, role management, and assisted sales workflows.

## Stack
- React 18, TypeScript, Vite
- Java 21, Spring Boot 3.4, Spring Security, Spring Data JPA
- PostgreSQL 16
- Groq API for optional buyer summaries and reply drafts; deterministic fallback when no API key is configured
- Docker Compose for local PostgreSQL

## Quick start (Windows)

Prerequisites: Java 21, Maven 3.9+, Node.js 20+, npm, and Docker Desktop.

1. Open a terminal in the project root and start the database:
   ```powershell
   docker compose up -d db
   ```
2. Start the backend in terminal 1:
   ```powershell
   cd backend
   $env:DB_URL="jdbc:postgresql://localhost:5432/autosphere"
   $env:DB_USERNAME="autosphere"
   $env:DB_PASSWORD="autosphere_local"
   $env:JWT_SECRET="local-development-secret-change-this-to-at-least-32-characters"
   $env:GROQ_API_KEY=""
   mvn spring-boot:run
   ```
3. Start the frontend in terminal 2:
   ```powershell
   cd frontend
   npm ci
   npm run dev
   ```
4. Open http://localhost:5173. API health: http://localhost:8080/api/health.

A root `.env.example` is provided. The commands above set the backend environment directly for Windows PowerShell. `npm ci` installs platform-specific native packages; do not reuse a `node_modules` folder copied from another operating system.

## Demo accounts
- Buyer: `buyer@autosphere.demo` / `Buyer@123`
- Sales consultant: `sales@autosphere.demo` / `Sales@123`
- Used-car manager: `usedcars@autosphere.demo` / `UsedCars@123`
- Admin: `admin@autosphere.demo` / `Admin@123`

All records and credentials are synthetic. Change credentials and secrets before any public deployment.

## Implemented workflows

### Buyer
- Browse and filter new and certified pre-owned inventory.
- Open a vehicle-specific detail page with specifications, colours, used-car owner count, masked registration and inspection notes.
- Calculate city-wise on-road prices from stored admin price components (ex-showroom + road tax + insurance + accessories), with a validity date and admin note.
- View indicative EMI based on principal, down payment, annual rate and tenure.
- Request a test drive by showroom, date and vehicle/demo car; available slots are checked against existing bookings and duplicate vehicle slots are rejected.
- Request service by workshop, date and slot. The demo enforces a capacity of three requests per workshop/time slot.
- Submit a trade-in valuation request.
- Provide consent on personal-data forms and submit an opt-out request.

### Sales / lead desk
- Lead inbox with status updates, assigned consultant, showroom, consent, acknowledgement and age/SLA display.
- Automatic assignment rules based on showroom and used-car model category.
- CSV import endpoint for legacy lead exports. Expected columns: `name,email,phone,vehicleModel,message,showroom`. This lightweight importer expects unquoted comma-separated fields.
- AI-assisted buyer summary and editable first-reply draft. Drafts are never sent automatically.
- Opt-out status is recorded on matching lead records.

### Used-car manager
- Separate `USED_CAR_MANAGER` role.
- Comparable-sales price band endpoint restricted to used-car manager and admin roles.
- Suggestions use seeded synthetic historical sales matched by model, nearby year and kilometre range. They are not a valuation guarantee.

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
```bash
cd backend
mvn test
```
Frontend:
```bash
cd frontend
npm ci
npm test
npm run build
```
The source ZIP does not include `node_modules` or Maven `target` output. Run `npm ci` on the target OS. The TypeScript project check passed during preparation. Vitest/Vite bundling could not run in the preparation container because the ZIP-provided Windows `node_modules` lacked the Linux Rollup optional binary; Maven was not installed in the container. Run `npm ci`, `npm test`, `npm run build`, and `mvn test` on Windows before submission.

## Demo boundaries / production work
- WhatsApp/SMS acknowledgement is recorded as a **SIMULATED** template event; no external messaging provider is connected.
- Test-drive and service slots are database-backed demo workflows. Service capacity is a demo count check; production requires transactional locking/reservation expiry and actual bay/demo-car calendars.
- Lead SLA is displayed and assigned; no telephony integration automatically calls a consultant.
- CSV import is intentionally small and accepts unquoted comma-separated rows. Production import should use a robust CSV parser, validation report and duplicate detection.
- Historical used-car comparables are synthetic seed data. Do not use them as a real market valuation.
- Email/WhatsApp sending is not enabled. AI output is draft-only and must be reviewed by a human.
- No online payments or DMS integration are included. No real customer data should be entered.
- Replace sample Unsplash imagery with licensed dealership photography. Configure HTTPS, production secrets, rate limiting, audit logs, backups, refresh-token rotation, monitoring and privacy retention workflows before production use.
