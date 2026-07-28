# Career Portal — Backend (Phase 1 MVP)

Spring Boot 3 + MySQL backend implementing: JWT auth, multi-criteria job search
(JPA Specifications), resume upload + parsing (PDFBox), ATS match scoring,
tailored resume PDF generation (iText) with version history, a Kanban-style
application tracker, template-based cover letters, and a daily email digest
for saved searches.

## ⚠️ Before you run this

This code was written and reviewed carefully, but **could not be compiled in
the environment it was built in** (no access to Maven Central from that
sandbox). Run this first and fix anything that surfaces — treat it as a strong
first draft, not a guarantee:

```bash
mvn clean compile
```

Most likely spots for an issue, if any: exact iText 8 artifact coordinates
(`itext-core`), or jjwt 0.12.x API surface (`JwtUtil.java`) — both were written
against current documentation but library APIs shift between versions.

## 1. Set up MySQL

**Option A — Docker (recommended):**
```bash
docker compose up -d
```
This starts MySQL and runs `schema.sql` automatically on first boot.

**Option B — Local MySQL:**
```bash
mysql -u root -p < schema.sql
```

## 2. Configure `application.properties`

Edit `src/main/resources/application.properties`:
- `spring.datasource.password` → your MySQL password
- `app.jwt.secret` → generate one: `openssl rand -base64 48`
- `spring.mail.username` / `spring.mail.password` → only needed once you wire up
  the daily digest; safe to leave as placeholders until then (Gmail needs an
  [App Password](https://myaccount.google.com/apppasswords), not your normal password)

## 3. Run it

```bash
mvn spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui.html` — explore and test every
endpoint from the browser, including auth.

## Build order (matches the plan from before — follow this, don't skip ahead)

1. **Auth** — `POST /api/auth/register`, `POST /api/auth/login`. Test in Swagger,
   confirm you get a JWT back, and confirm using it on a protected endpoint works.
2. **Jobs** — `POST /api/jobs` to manually add a couple of test jobs, then
   `GET /api/jobs/search?keyword=java&location=chennai` to see the Specification
   filtering work.
3. **Resume upload** — `POST /api/resumes/upload` (multipart) with a real PDF resume.
   Check the response lists skills it recognized — this is `ResumeParserService`
   + `SkillDictionary` working together.
4. **ATS match** — `GET /api/resumes/{resumeId}/match?jobId=X` — the core feature.
   Read `AtsMatchService.java` line by line until you can explain it without
   looking; this is what an interviewer will ask about most.
5. **Resume versions** — `POST /api/resumes/{resumeId}/versions?jobId=X` generates
   a tailored PDF and records a version. `GET /api/resumes/versions` lists them.
6. **Applications (Kanban)** — `POST /api/applications`, `GET /api/applications`,
   `PATCH /api/applications/{id}/status`.
7. **Cover letters** — `GET /api/cover-letters/generate?jobId=X`.
8. **Email digest** — wire up real SMTP credentials, add a `SavedSearch` row
   manually in MySQL, and either wait for 8 AM or temporarily change the cron
   expression to run sooner so you can see it work end to end.

## Deliberately deferred to Phase 2 (don't add until Phase 1 fully works)

- Redis caching
- Spring Batch / Quartz (the single `@Scheduled` job is enough at this scale)
- Jsoup-based scraping — only point this at a source that permits it
  (e.g. a legitimate public job API), not portal ToS-restricted sites
- React frontend — build once this API is stable, not before
- Admin-only restriction on `POST /api/jobs` (currently open to any authenticated
  flow for MVP simplicity — add a `hasRole('ADMIN')` check before this goes anywhere real)

## One licensing note

`itext-core` is AGPL-licensed (with a paid commercial alternative). That's
fine for a personal/portfolio project, but if this ever becomes something you'd
distribute commercially, switch `ResumeGeneratorService` to **OpenPDF**
(LGPL/MPL, no AGPL obligations) — the original brief listed both as options for
exactly this reason.
