# Milestone 1 Progress

## 2026-08-02

- Read the full governing agreement, M1 specification, and all `.ai` context.
- Confirmed the repository had no implementation or commits.
- Added repository configuration, PostgreSQL Compose service, Flyway schema, Spring domain/API/integration boundary, durable LangGraph workflow, tests, local scripts, and initial documentation.
- Next: provision Python 3.12, generate Maven wrapper, run both test suites, run the real service smoke test, repair issues, and record final evidence.

## Completion evidence

- Added six focused commits during implementation rather than one final bulk commit.
- Backend clean suite: 5/5 passing against Testcontainers PostgreSQL 16.
- Orchestrator suite: 3/3 passing on Python 3.12.
- Live smoke: PASS for waiting → resolve → exactly one Product Brief → completed, including invalid and duplicate resolution checks.
- Restart proof: PASS after stopping and restarting Spring and FastAPI between interrupt and resume.
- Local port 55432 was used because an unrelated existing Docker container owns 5432; Compose retains 5432 as its default.
- Exact next task: define Milestone 2 Slack interaction and security contracts.

# Milestone 2 Progress

## 2026-08-02

- Founder approved Slack as the FounderOS team workspace with functional/project channels, direct messages, specialist personas, and a coordinating Chief of Staff.
- Added and committed the M2 specification before implementation.
- Added Flyway-owned Agent Profile, channel binding, conversation, task, inbox, outbox, and opaque decision-action records.
- Added raw-body Slack HMAC verification, five-minute replay protection, workspace/founder allow-lists, bot-loop filtering, and event deduplication.
- Added deterministic channel, thread, mention, and DM-prefix routing with exactly one accountable agent per root task.
- Added PostgreSQL queue workers, restart recovery, bounded retry, `chat.postMessage`, and `chat.update` delivery intents.
- Connected opaque Slack decision buttons to the existing authoritative Founder Decision resume flow.
- Protected Slack administration APIs with a separate constant-time-checked admin token.
- Added a Slack App manifest, setup/operations guide, and integration tests.

## Verification evidence

- `cd backend && ./mvnw test`: PASS, 8 tests (5 existing M1/domain tests plus 3 M2 integration tests).
- `cd orchestrator && .venv/bin/pytest -q`: PASS, 3 tests; the existing upstream LangGraph pending-deprecation warning remains.
- M2 integration proof covers valid/invalid signatures, stale requests, unauthorized users, bot loops, duplicate events, functional-channel routing, DM persona routing, durable task creation, outbox delivery, decision publication/resolution, original-message update intent, and duplicate clicks.
- Real Slack workspace installation and delivery were not run because no Slack credentials or public callback URL were provided.

## Exact next task

Install the app from `slack-app-manifest.yml` in the founder's Slack workspace, configure the five secrets/identifiers, bind the initial channels, and run the first real workspace smoke test. Repair only integration differences found against the real Slack payloads before beginning M3 intelligence.
