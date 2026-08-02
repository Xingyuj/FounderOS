# Current Task

## Objective

Define Milestone 2 as a Slack Team Experience in which the founder collaborates through functional/project channels and direct messages with a small agent team coordinated by a Chief of Staff.

## Current status

M1 remains complete. The local M2 Slack Team Experience implementation is complete and automated tests pass. Real Slack workspace installation and delivery remain unverified because credentials and a public HTTPS callback were not available.

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

## M2 implementation choices

- One FounderOS Slack App represents five visible agent personas; DMs select a specialist through an explicit English or Chinese role prefix and otherwise route to Chief of Staff.
- No slash command is required. M2 uses channel messages, app mentions, DMs, and interactive decision buttons.
- Bot scopes are `app_mentions:read`, `channels:history`, `groups:history`, `im:history`, and `chat:write`; the app must be invited into every working channel.
- PostgreSQL inbox/outbox workers provide asynchronous processing, idempotency, retry, and restart recovery without Redis or Kafka.

## Exact next task

Install the manifest in the founder's Slack workspace, configure `SLACK_SIGNING_SECRET`, `SLACK_BOT_TOKEN`, `SLACK_TEAM_ID`, `SLACK_FOUNDER_USER_ID`, and `SLACK_ADMIN_TOKEN`, expose the two signed ingress endpoints over HTTPS, bind initial channels, and run a real Slack smoke test.
