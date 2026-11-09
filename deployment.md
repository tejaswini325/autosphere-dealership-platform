# AutoSphere deployment guide

## Local run
1. Install Java 21, Node.js 20+, Maven 3.9+ and Docker Desktop.
2. Copy `.env.example` to `.env` and replace secrets for your environment.
3. Run `docker compose up -d db` from the repository root.
4. Start the API from `backend` with `mvn spring-boot:run` after setting `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and `JWT_SECRET`.
5. Start the UI from `frontend` using `npm install` and `npm run dev`.

## Deployment outline
- Frontend: deploy `frontend` to Vercel. Set `VITE_API_BASE_URL` to the deployed API URL plus `/api`.
- Backend: deploy `backend` as a Java 21 service on Render or another Java host.
- Database: use managed PostgreSQL in the same region as the API.
- Set the following environment variables on the backend host (names only): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `GROQ_API_KEY`, `GROQ_MODEL`, `CORS_ALLOWED_ORIGINS`.
- Set `VITE_API_BASE_URL` on the frontend host.
- Set `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` after introducing reviewed Flyway migrations. The starter currently uses `update` for quick local setup; do not retain this setting for a production database.

## Reviewer demo logins
- Buyer: `buyer@autosphere.demo` / `Buyer@123`
- Sales: `sales@autosphere.demo` / `Sales@123`
- Admin: `admin@autosphere.demo` / `Admin@123`

These accounts are seeded for local review. Recreate equivalent synthetic accounts in the deployed environment and change all passwords before sharing the app publicly. Do not place real customer information in the demo database.


## Added demo workflows
- Public buyer routes: `/service`, `/trade-in`, `/privacy/opt-out`.
- Sales/admin: lead CSV import, five-minute age indicator, buyer summary and editable reply draft.
- Used-car manager: `/used-manager` (demo login `usedcars@autosphere.demo`).
- Admin: vehicle status, city pricing and validity note, offers/banners, role management.
- Test-drive availability: `GET /api/bookings/availability?vehicleId=1&date=YYYY-MM-DD`.

## Demo integration boundaries
SMS/WhatsApp acknowledgement is simulated and stored with the lead; no external messaging provider or telephony is connected. Workshop capacity is a simple database count check, not a production-grade transactional bay reservation. Used-car price bands use synthetic seeded sale comparables. Do not use the demo with real customer information.
