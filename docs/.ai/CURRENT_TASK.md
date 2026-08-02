# Current Task

## Objective

Define Milestone 2 as a Slack Team Experience in which the founder collaborates through functional/project channels and direct messages with a small agent team coordinated by a Chief of Staff.

## Current status

M1 remains complete. On 2026-08-02 the founder approved the M2 product direction and `FOUNDER_OS_M2.md` was written. M2 implementation has not started.

## Implementation plan

1. Bootstrap repository configuration, PostgreSQL Compose service, Maven/Spring Boot backend, and Python/FastAPI orchestrator.
2. Add Flyway-owned business schema, domain services, RFC 7807 APIs, and backend-to-orchestrator coordination.
3. Implement the deterministic LangGraph interrupt/resume workflow with PostgreSQL checkpoint persistence.
4. Add backend unit/integration tests and orchestrator workflow/API tests.
5. Start PostgreSQL and both services, execute the HTTP smoke test including negative/idempotency checks, and repair failures.
6. Align README/API/architecture/milestone/progress documentation and complete all `.ai` operational records with evidence.

## Recommended execution sequence

1. Repository skeleton and developer tooling.
2. Docker Compose PostgreSQL.
3. Spring Boot domain model, Flyway migrations, APIs, and tests.
4. FastAPI/LangGraph pause-resume workflow and checkpoint persistence.
5. Backend-to-orchestrator coordination.
6. End-to-end smoke test.
7. Documentation and final handoff.

## Completion target

The founder can create a project over HTTP, observe a persisted open decision, resolve it later, and receive exactly one stored Product Brief after the workflow resumes.

## Verification evidence

- `cd backend && ./mvnw clean test`: 5 passed.
- `cd orchestrator && .venv/bin/pytest`: 3 passed; one upstream LangGraph pending-deprecation warning.
- `POSTGRES_PORT=55432 docker compose up -d postgres`: healthy PostgreSQL 16 (alternate port used because an unrelated existing container owns host port 5432).
- `./scripts/smoke-test.sh`: PASS with project `6ed929bd-a3b8-4945-a221-5118cd73dbcc` and matching persisted workflow thread.
- Restart proof: project `0f25cf3d-e99e-409c-9eab-2811b8653337` paused, both application processes restarted, then decision `fcb7b9b2-3b82-4b91-868b-993a2882cbb9` resumed to COMPLETED.

## Exact next task

Review and freeze the deferred Slack implementation choices in `FOUNDER_OS_M2.md`, especially the Slack-supported DM/persona model and resulting least-privilege OAuth scopes. Then implement the M2 persistence model and security-first Slack ingress; do not begin with agent intelligence or Slack message generation.
