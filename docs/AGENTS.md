# FounderOS Agent Agreement

## 1. Mission

Build FounderOS as a durable, human-in-the-loop operating system for a one-person company. The system must autonomously advance approved work, pause at material founder decisions, preserve state, and resume without losing context.

The current delivery target is defined in `FOUNDER_OS_M1.md`. Milestone 1 proves only:

1. A project idea can be submitted.
2. The workflow creates one founder decision.
3. Execution pauses and persists.
4. The founder resolves the decision.
5. Execution resumes from the same checkpoint.
6. A Product Brief is stored as an artifact.

Do not expand scope unless the founder explicitly approves it.

## 2. Source of truth and reading order

Before changing anything, read in this order:

1. `AGENTS.md`
2. `FOUNDER_OS_M1.md`
3. `.ai/PROJECT_CONTEXT.md`
4. `.ai/CURRENT_TASK.md`
5. `.ai/DECISIONS.md`
6. `.ai/HANDOFF.md`
7. `README.md`, relevant docs, existing code, `git status`, and recent commits

Priority when instructions conflict:

1. Direct founder instruction in the current task
2. `FOUNDER_OS_M1.md`
3. `AGENTS.md`
4. Recorded decisions in `.ai/DECISIONS.md`
5. Existing implementation and documentation

Never silently resolve a material conflict. Record it as a blocker or founder decision.

## 3. Operating principles

- Work toward a verifiable outcome, not merely code generation.
- Prefer the smallest end-to-end implementation that satisfies acceptance criteria.
- Preserve architectural boundaries.
- Keep changes reviewable and reversible.
- Do not claim completion without evidence from builds, tests, or a working smoke test.
- Do not hide failures, skipped tests, assumptions, or incomplete work.
- Do not replace working code solely for stylistic preference.
- Do not add speculative infrastructure or abstractions.
- Never commit secrets, tokens, credentials, private keys, or local environment files.

## 4. Authority model

Agents may autonomously:

- inspect the repository;
- create implementation plans;
- add or modify code within the approved milestone;
- add migrations, tests, scripts, and documentation required by the milestone;
- fix build and test failures caused by their changes;
- make low-risk, reversible implementation choices consistent with approved architecture.

Agents must stop and request a founder decision before:

- changing milestone scope or product intent;
- introducing paid services or external accounts;
- changing the approved primary technology stack;
- weakening security, validation, auditability, or data integrity;
- deleting persistent data or rewriting migration history;
- publishing, deploying, merging, or releasing externally;
- making a difficult-to-reverse architectural choice not covered by existing decisions;
- continuing after two unsuccessful repair cycles for the same blocker.

## 5. Architecture contract

Milestone 1 uses a monorepo:

- `backend/`: Java 21, Spring Boot 3.x, business APIs and authoritative business records.
- `orchestrator/`: Python 3.12+, FastAPI and LangGraph workflow execution.
- PostgreSQL 16: durable business data and workflow checkpoints.
- `docs/`: architecture, API, and milestone documentation.

The Spring Boot backend is the source of truth for:

- Project
- FounderDecision
- Artifact
- WorkflowRun

The Python service owns workflow execution and checkpointing, not authoritative business history.

Avoid cyclic synchronous service calls. For M1, the client calls Spring Boot; Spring Boot coordinates the orchestrator.

Do not add Slack, WhatsApp, frontend, Redis, Kafka, Kubernetes, Terraform, authentication, Sub2API, or multiple agent roles in M1.

## 6. Implementation standards

### General

- Use clear domain names rather than generic utility abstractions.
- Validate all external input.
- Return stable, documented error responses.
- Use UTC timestamps in storage and APIs.
- Generate identifiers server-side.
- Make workflow resume operations idempotent where practical.
- Keep public APIs separate from internal service APIs.

### Java

- Java 21 and current Spring Boot 3.x compatible code.
- Constructor injection only.
- Controllers contain transport concerns, not business logic.
- Transactions belong in application/service boundaries.
- Use Flyway for all schema changes; never rely on Hibernate schema auto-generation.
- Use DTOs for API contracts rather than exposing JPA entities.
- Add unit tests for domain/application logic and Testcontainers integration tests for persistence/API boundaries.

### Python

- Python 3.12+ with typed functions and Pydantic v2 models.
- Keep LangGraph nodes small and deterministic around side effects.
- A node that can be replayed after an interrupt must not duplicate non-idempotent work.
- Isolate repository, workflow, transport, and configuration concerns.
- Use pytest and httpx for tests.
- Never use an in-memory checkpointer as the final M1 implementation.

### Database

- PostgreSQL 16.
- Use explicit constraints, foreign keys, indexes, and sensible uniqueness rules.
- Store decision options as JSONB only where variable structure is required.
- Do not edit an applied Flyway migration; add a new migration.

## 7. Required workflow behavior

The workflow is:

`START -> analyse_idea -> create_founder_question -> wait_for_founder -> generate_product_brief -> END`

Requirements:

- Initial execution returns or exposes one founder decision and pauses.
- The project becomes `WAITING_FOR_FOUNDER`.
- The workflow thread/checkpoint identifier is persisted.
- Resolving the decision records the selected option and optional founder comment.
- Resume uses the same thread/checkpoint.
- Repeated resolution must not generate duplicate artifacts.
- Successful resume creates version 1 of a `PRODUCT_BRIEF` artifact and marks the project/workflow complete.

The first implementation may use deterministic question/brief generation. Model integration is not required for M1.

## 8. Testing and evidence

Before declaring completion, run all applicable checks:

```bash
# Backend
cd backend && ./mvnw test

# Orchestrator
cd orchestrator && pytest

# Repository smoke test
./scripts/smoke-test.sh
```

If wrapper or script names differ, document and use the actual commands.

The smoke test must prove:

1. Services start.
2. A project is created.
3. An open founder decision exists.
4. The project is waiting.
5. The decision is resolved.
6. The workflow resumes.
7. Exactly one Product Brief artifact exists.
8. The final project state is completed.

A completion report must include:

- files changed;
- architecture decisions made;
- tests and commands run;
- pass/fail results;
- known limitations;
- exact next recommended task.

## 9. Git discipline

- Inspect `git status` before and after work.
- Do not discard or overwrite unrelated user changes.
- Keep generated build output out of Git.
- Use focused commits when the founder asks for commits.
- Suggested commit format: `feat(m1): ...`, `test(m1): ...`, `docs(m1): ...`, `fix(m1): ...`.
- Do not push, merge, rebase shared history, or force-push without explicit approval.

## 10. Context maintenance

After meaningful progress, update:

- `.ai/CURRENT_TASK.md`: current status, completed and remaining work.
- `.ai/DECISIONS.md`: durable technical or product decisions and rationale.
- `.ai/HANDOFF.md`: exact state for the next agent/session.

Do not use these files as a substitute for proper code or product documentation. They are operational memory.

## 11. Definition of done

M1 is done only when:

- implementation satisfies `FOUNDER_OS_M1.md`;
- both services run locally through documented commands;
- persistence survives process restart where required by the milestone;
- automated tests pass;
- the smoke test demonstrates the complete pause/resume path;
- docs match actual behavior;
- no secrets are committed;
- `.ai/HANDOFF.md` contains a truthful final handoff.

When blocked, stop at a clean state and report the smallest concrete decision or external input required.
