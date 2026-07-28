# Career Portal — Full Stack (Phase 1 MVP)

Two folders, one app:
- `backend/` — Spring Boot 3 + MySQL API (JWT auth, job search, ATS resume matching, resume versions, Kanban applications, cover letters, daily email digest)
- `frontend/` — React + Vite + Tailwind, talks to the backend over REST

**Status: backend is a strong first draft I could not compile in the sandbox
this was built in (no Maven Central access there) — check it with
`mvn clean compile` first. The frontend WAS built and verified: `npm run build`
completed with zero errors before this was packaged.**

---

## Run order (backend first, always)

### 1. Start MySQL + the backend
```bash
cd backend
docker compose up -d          # starts MySQL, loads schema.sql
# edit src/main/resources/application.properties:
#   - spring.datasource.password
#   - app.jwt.secret  (generate: openssl rand -base64 48)
mvn clean compile             # fix anything that surfaces here first
mvn spring-boot:run
```
Confirm it's up: open `http://localhost:8080/swagger-ui.html`

### 2. Start the frontend
```bash
cd frontend
npm install
npm run dev
```
Opens at `http://localhost:5173` by default. It's already configured (via
`.env`) to call the backend at `http://localhost:8080/api` — if you run the
backend on a different port, update `frontend/.env`.

---

## First run walkthrough

1. Open the frontend, click **Register**, create an account
2. You'll land on the **Jobs** tab — it'll be empty. Add a couple of test
   jobs via Swagger UI (`POST /api/jobs`) on the backend, since there's no
   "add job" screen in Phase 1 (job creation is meant to come from an
   aggregator/admin flow later, not end users)
3. Refresh **Jobs** — search and filter should now show results
4. Click **Save to Applications** on a job — check the **Applications** tab,
   you should see it appear in the "Saved" column, and you can drag it across
   columns (Kanban board)
5. Go to **Resume & ATS Match**, upload a real PDF resume — you should see
   detected skills
6. Copy a Job ID from the Jobs tab, paste it in, click **Compute Match
   Score** — this is the core feature, read `AtsMatchService.java` in the
   backend alongside this to understand exactly what it's doing
7. **Cover Letter** tab — paste a Job ID, generate a template letter

---

## What's real vs. simplified in this frontend

- **Real**: routing, auth (JWT stored client-side, attached via axios
  interceptor), all four core screens wired to real backend endpoints,
  optimistic drag-and-drop on the Kanban board with rollback on failure
- **Simplified for Phase 1**: no job-creation UI (use Swagger), no PDF
  download button wired up yet (the backend endpoint exists —
  `GET /api/resumes/versions/{id}/download` — add a button calling it as
  your next small task), no analytics dashboard (Recharts trending-tech-stack
  view from the original brief is a Phase 2 item, same as Redis/Batch/Quartz
  on the backend side)

## Drag-and-drop library note

The original brief specified `react-beautiful-dnd`, but that package is
unmaintained and breaks under React 18+ StrictMode. This uses
**`@hello-pangea/dnd`** instead — a community-maintained fork with an
identical API, so nothing about how the Kanban board works is different,
it just actually keeps working.
