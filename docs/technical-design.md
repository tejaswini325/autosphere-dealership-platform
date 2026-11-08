# AutoSphere — Technical Design Document (working draft)

> Add the final public GitHub URL and deployed application URL at the top before submission. This is a working Markdown draft; convert it to PDF or Word for the Isocode submission.

## 1. Problem and scope
AutoSphere is a dealership website and sales workspace for one authorised passenger-car brand. The MVP implements the assigned core slice: new/used vehicle pages with on-road pricing, test-drive requests, lead inbox with automatic assignment, admin inventory and pricing, and AI-assisted buyer summaries/reply drafting.

## 2. Architecture and rationale
- **React + TypeScript + Vite:** responsive buyer-facing catalogue and role-based workspaces; component-based UI and typed API contracts.
- **Spring Boot 3 / Java 21:** REST API, validation, authentication, deterministic pricing, booking persistence, lead assignment and AI provider adapter. Java/Spring offers explicit service boundaries and testable business rules.
- **PostgreSQL:** relational integrity for vehicles, city pricing, bookings, users and leads. A unique constraint prevents duplicate requests for the same vehicle/date/time.
- **JWT + Spring Security:** stateless authentication and role checks for buyer, sales and admin actions.
- **Groq (optional):** generates a first-reply draft. A deterministic fallback keeps the workflow usable without credentials or during provider failure. Drafts are never sent automatically.

### Request flow
Browser → REST API → Spring Security/JWT → controller validation → domain service/repository → PostgreSQL. AI draft requests call Groq only on the server; API keys are never exposed to the browser.

## 3. Data model
- `app_users`: name, unique email, password hash, role.
- `vehicles`: stock ID, type, model, variant, fuel, year, kilometres, ex-showroom price, status, image and description.
- `city_prices`: vehicle, city, road tax, insurance and accessories; unique per vehicle/city.
- `test_drive_bookings`: customer contact, vehicle, date, time, showroom and status; unique vehicle/date/time constraint.
- `leads`: customer contact, message, vehicle interest, status, assignee and creation timestamp.

## 4. Main flows
1. **Browse:** public user lists and filters vehicles, then opens a detail page.
2. **Price:** UI requests a city quote. Backend adds ex-showroom price and admin-configured road tax, insurance and accessories. AI is not involved.
3. **Test drive:** user submits contact details, vehicle, date, time and showroom. Backend validates future date and relies on a database uniqueness constraint to reject duplicate slots.
4. **Enquiry:** user submits a lead. Backend stores it and assigns it to the configured sales queue/consultant.
5. **Sales:** authenticated sales/admin user views leads and changes status.
6. **AI draft:** sales user requests a draft; provider output is returned as editable text. Provider errors fall back to a clearly labeled template. No automatic send action exists.
7. **Admin:** admin adds vehicles and configures city pricing components.

## 5. AI behavior and failure handling
The AI feature drafts a first response using the buyer's message and selected model. The prompt prohibits inventing prices, discounts, availability or commitments. The UI labels the result as a draft and requires human review. If no Groq key is configured or the provider call fails, a deterministic fallback is returned. On-road pricing is exclusively calculated from database values.

## 6. UI decisions
- Public catalogue uses large vehicle imagery, compact filters and visible starting prices.
- Detail view places vehicle specifications beside an on-road price panel and enquiry/test-drive actions.
- Sales workspace separates the inbox list from the selected lead details to reduce navigation.
- Admin separates vehicle creation from city price configuration.
- Responsive layouts collapse grids and navigation for mobile screens. Loading, empty, success and error feedback are included in key flows.

## 7. Assumptions and limitations
- Brand X and the sample inventory are represented with synthetic model/stock data.
- Lucknow, Kanpur and Varanasi are seeded example cities; tax/insurance/accessory values are illustrative and not legal or commercial quotes.
- A test-drive request is not a confirmed appointment; dealership staff must confirm it. The starter uniqueness rule is per vehicle/date/time, not a full bay-capacity scheduler.
- Lead assignment currently uses a seeded consultant as the default queue. Production assignment should use showroom/model workload rules and response-time monitoring.
- AI draft generation is optional and requires a server-side API key.
- Payments, DMS integrations, portal CSV import, service booking, trade-in and WhatsApp/SMS delivery are not implemented in this core MVP.

## 8. Next steps
Add full slot capacity/locking, showroom/model routing rules, CSV lead import, used-car valuation suggestions with manager-only visibility, DPDP consent and retention controls, audit events, rate limiting, refresh-token rotation, database migrations, production observability and accessibility review.
