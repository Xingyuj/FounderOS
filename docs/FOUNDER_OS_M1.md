# FounderOS Milestone 1

## Objective

Build the first end-to-end FounderOS workflow:

```text
Founder submits a project idea
        ↓
FounderOS creates a project
        ↓
Workflow generates one founder decision
        ↓
Workflow pauses and persists its state
        ↓
Founder submits a decision through HTTP
        ↓
Workflow resumes from the same checkpoint
        ↓
FounderOS generates and stores a Product Brief
```

Do **not** implement Slack, WhatsApp, multiple agents, autonomous coding, Sub2API routing, or a frontend in this milestone.

The purpose of Milestone 1 is to prove:

1. Project state is persisted.
2. A workflow can pause for founder input.
3. The workflow can resume later from the same position.
4. The founder decision is stored as an auditable record.
5. A project artifact is generated after the decision.

---

# Repository Structure

Create the following monorepo structure:

```text
founder-os/
├── README.md
├── AGENTS.md
├── .gitignore
├── .env.example
├── docker-compose.yml
│
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/founderos/backend/
│       │   └── resources/
│       └── test/
│
├── orchestrator/
│   ├── pyproject.toml
│   ├── app/
│   │   ├── __init__.py
│   │   ├── main.py
│   │   ├── graph.py
│   │   ├── schemas.py
│   │   ├── settings.py
│   │   └── repository.py
│   └── tests/
│
├── docs/
│   ├── architecture.md
│   ├── milestones.md
│   └── api.md
│
└── scripts/
    ├── start-local.sh
    └── smoke-test.sh
```

---

# Technology Stack

## Backend

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- Spring Boot Actuator
- Testcontainers for integration tests

## Orchestrator

- Python 3.12+
- FastAPI
- LangGraph
- PostgreSQL-backed LangGraph checkpointer if practical
- Pydantic v2
- SQLAlchemy or psycopg
- pytest
- httpx

## Infrastructure

- Docker Compose
- PostgreSQL 16

Do not add Redis, Kafka, Kubernetes, Terraform, authentication, or cloud deployment yet.

---

# Architectural Boundary

The Spring Boot backend is the source of truth for business data:

- Project
- FounderDecision
- Artifact
- WorkflowRun

The Python orchestrator owns workflow execution:

- Start project discovery workflow
- Pause for founder decision
- Resume workflow
- Generate Product Brief

The orchestrator must not become the authoritative store for business records. It may use LangGraph checkpoints for execution state, but business state must also be recorded through backend APIs or a clearly separated shared database repository.

For Milestone 1, direct database access from both services is acceptable only if repository ownership is clearly documented. Preferred design:

```text
Client
  ↓
Spring Boot API
  ↓
Python Orchestrator API
  ↓
LangGraph

Spring Boot owns Project, Decision, Artifact, WorkflowRun records.
```

Avoid cyclic synchronous calls.

Recommended flow:

```text
POST /api/projects
  Spring Boot creates Project
  Spring Boot calls orchestrator POST /internal/workflows
  Orchestrator runs until interrupt
  Orchestrator returns decision payload
  Spring Boot persists FounderDecision and WorkflowRun state

POST /api/decisions/{id}/resolve
  Spring Boot persists founder response
  Spring Boot calls orchestrator POST /internal/workflows/{threadId}/resume
  Orchestrator resumes and returns Product Brief
  Spring Boot persists Artifact and marks workflow completed
```

---

# Domain Model

## Project

Fields:

```text
id                  UUID
name                VARCHAR(150)
idea                TEXT
status              ENUM/VARCHAR
workflowThreadId    VARCHAR(200), nullable until workflow starts
createdAt           TIMESTAMPTZ
updatedAt           TIMESTAMPTZ
```

Statuses:

```text
DISCOVERY
WAITING_FOR_FOUNDER
PRODUCT_DEFINITION
COMPLETED
FAILED
```

## FounderDecision

Fields:

```text
id                  UUID
projectId           UUID
workflowThreadId    VARCHAR(200)
question            TEXT
options             JSONB
recommendation      TEXT nullable
context             TEXT nullable
status              ENUM/VARCHAR
selectedOption      TEXT nullable
founderComment      TEXT nullable
createdAt           TIMESTAMPTZ
resolvedAt          TIMESTAMPTZ nullable
```

Statuses:

```text
OPEN
RESOLVED
CANCELLED
```

## Artifact

Fields:

```text
id                  UUID
projectId           UUID
type                VARCHAR(100)
title               VARCHAR(255)
content             TEXT
version             INTEGER
createdAt           TIMESTAMPTZ
```

Artifact type for this milestone:

```text
PRODUCT_BRIEF
```

## WorkflowRun

Fields:

```text
id                  UUID
projectId           UUID
threadId            VARCHAR(200)
status              ENUM/VARCHAR
currentNode         VARCHAR(150)
errorMessage        TEXT nullable
createdAt           TIMESTAMPTZ
updatedAt           TIMESTAMPTZ
```

Statuses:

```text
RUNNING
WAITING_FOR_FOUNDER
COMPLETED
FAILED
```

---

# Backend API

Base path:

```text
/api
```

## 1. Create project

```http
POST /api/projects
Content-Type: application/json
```

Request:

```json
{
  "name": "Tradigo",
  "idea": "A platform that helps retail investors design and evaluate trading strategies without writing code."
}
```

Expected behavior:

1. Validate input.
2. Create Project with status `DISCOVERY`.
3. Generate or receive a workflow thread ID.
4. Start the orchestrator workflow.
5. Persist the resulting open FounderDecision.
6. Update Project to `WAITING_FOR_FOUNDER`.
7. Return project and decision.

Response shape:

```json
{
  "project": {
    "id": "uuid",
    "name": "Tradigo",
    "status": "WAITING_FOR_FOUNDER",
    "workflowThreadId": "tradigo-uuid"
  },
  "decision": {
    "id": "uuid",
    "question": "Who should the first version serve?",
    "options": [
      "Beginner investors",
      "Experienced non-programmers",
      "Professional quant traders"
    ],
    "recommendation": "Experienced non-programmers",
    "status": "OPEN"
  }
}
```

## 2. Get project

```http
GET /api/projects/{projectId}
```

Return project, open decisions, workflow status, and artifacts.

## 3. Resolve founder decision

```http
POST /api/decisions/{decisionId}/resolve
Content-Type: application/json
```

Request:

```json
{
  "selectedOption": "Experienced non-programmers",
  "comment": "Focus on Australian users initially."
}
```

Expected behavior:

1. Reject if decision is not `OPEN`.
2. Reject if selected option is not present in the stored option list.
3. Persist selection and comment.
4. Mark decision `RESOLVED`.
5. Update project to `PRODUCT_DEFINITION`.
6. Resume the same workflow using the stored thread ID.
7. Persist returned Product Brief as Artifact version 1.
8. Mark WorkflowRun and Project `COMPLETED`.

Response shape:

```json
{
  "projectId": "uuid",
  "status": "COMPLETED",
  "artifact": {
    "id": "uuid",
    "type": "PRODUCT_BRIEF",
    "title": "Tradigo Product Brief",
    "version": 1,
    "content": "# Tradigo Product Brief\n..."
  }
}
```

## 4. Get artifact

```http
GET /api/artifacts/{artifactId}
```

---

# Orchestrator Internal API

Base path:

```text
/internal
```

## Start workflow

```http
POST /internal/workflows
```

Request:

```json
{
  "projectId": "uuid",
  "threadId": "tradigo-uuid",
  "projectName": "Tradigo",
  "idea": "A platform that helps retail investors design and evaluate trading strategies without writing code."
}
```

Response when interrupted:

```json
{
  "threadId": "tradigo-uuid",
  "status": "WAITING_FOR_FOUNDER",
  "currentNode": "wait_for_founder",
  "decision": {
    "question": "Who should the first version serve?",
    "options": [
      "Beginner investors",
      "Experienced non-programmers",
      "Professional quant traders"
    ],
    "recommendation": "Experienced non-programmers",
    "context": "The target segment affects the first product scope and onboarding flow."
  }
}
```

## Resume workflow

```http
POST /internal/workflows/{threadId}/resume
```

Request:

```json
{
  "selectedOption": "Experienced non-programmers",
  "comment": "Focus on Australian users initially."
}
```

Response:

```json
{
  "threadId": "tradigo-uuid",
  "status": "COMPLETED",
  "currentNode": "end",
  "artifact": {
    "type": "PRODUCT_BRIEF",
    "title": "Tradigo Product Brief",
    "content": "# Tradigo Product Brief\n..."
  }
}
```

---

# LangGraph Workflow

Implement this graph:

```text
START
  ↓
analyse_idea
  ↓
create_founder_question
  ↓
wait_for_founder
  ↓
generate_product_brief
  ↓
END
```

State schema:

```python
class FounderState(TypedDict):
    project_id: str
    thread_id: str
    project_name: str
    idea: str
    analysis: str | None
    decision: dict | None
    founder_response: dict | None
    product_brief: str | None
```

## Node requirements

### analyse_idea

For Milestone 1, do not call an LLM.

Return a deterministic analysis based on the project name and idea. Example:

```text
Tradigo helps retail investors create and evaluate trading strategies without coding.
The first blocking decision is the initial target customer segment.
```

### create_founder_question

Return this deterministic decision:

```json
{
  "question": "Who should the first version serve?",
  "options": [
    "Beginner investors",
    "Experienced non-programmers",
    "Professional quant traders"
  ],
  "recommendation": "Experienced non-programmers",
  "context": "The target segment affects product complexity, onboarding, and the MVP feature set."
}
```

### wait_for_founder

Use LangGraph `interrupt()`.

The interrupt payload must contain the complete decision object.

### generate_product_brief

Generate deterministic Markdown containing:

```markdown
# <Project Name> Product Brief

## Original Idea
<idea>

## Target User
<selectedOption>

## Founder Direction
<comment or "No additional direction provided.">

## Problem Statement
...

## Proposed MVP
- Strategy builder
- Basic validation
- Backtest summary
- Saved projects

## Non-Goals
- Automated trading
- Brokerage execution
- Professional quantitative research infrastructure

## Next Recommended Step
Produce an implementation-ready Product Requirements Document.
```

Do not use an LLM yet.

---

# Persistence Requirements

The workflow must survive orchestrator process restart if feasible within the chosen LangGraph persistence implementation.

At minimum:

1. `threadId` must be stable and stored in PostgreSQL.
2. The workflow must resume only with the matching `threadId`.
3. In-memory-only persistence is not acceptable for the final Milestone 1 implementation.
4. Document any limitations if PostgreSQL checkpoint integration requires a workaround.

Use a dedicated PostgreSQL schema or clearly named tables for LangGraph checkpoints.

---

# Database Migrations

Use Flyway in the Spring Boot backend.

Create migrations for:

```text
project
founder_decision
artifact
workflow_run
```

Use UUID primary keys.

Use JSONB for decision options.

Add indexes for:

```text
project.status
founder_decision.project_id
founder_decision.status
workflow_run.project_id
workflow_run.thread_id unique
artifact.project_id
```

---

# Docker Compose

Create a local PostgreSQL service:

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: founder_os
      POSTGRES_USER: founder_os
      POSTGRES_PASSWORD: founder_os
    ports:
      - "5432:5432"
    volumes:
      - founder_os_postgres:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U founder_os -d founder_os"]
      interval: 5s
      timeout: 5s
      retries: 10

volumes:
  founder_os_postgres:
```

The backend and orchestrator may initially run locally outside Docker.

---

# Configuration

Create `.env.example` with:

```dotenv
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=founder_os
POSTGRES_USER=founder_os
POSTGRES_PASSWORD=founder_os

BACKEND_PORT=8080
ORCHESTRATOR_PORT=8000
ORCHESTRATOR_BASE_URL=http://localhost:8000
BACKEND_BASE_URL=http://localhost:8080
```

No secrets should be committed.

---

# Error Handling

Implement explicit handling for:

- Invalid project input
- Orchestrator unavailable
- Duplicate decision resolution
- Invalid selected option
- Missing workflow thread
- Workflow resume failure
- Artifact persistence failure

Use RFC 7807 Problem Details in Spring Boot where practical.

Do not silently mark projects completed when any persistence step fails.

On workflow errors:

```text
Project.status = FAILED
WorkflowRun.status = FAILED
WorkflowRun.errorMessage = meaningful error text
```

---

# Testing Requirements

## Backend unit tests

Cover:

- Project creation validation
- FounderDecision option validation
- Duplicate decision resolution rejected
- Project status transitions

## Backend integration tests

Use Testcontainers PostgreSQL.

Cover:

1. Create project and persist decision.
2. Resolve decision and persist artifact.
3. Fetch completed project state.

The orchestrator API may be stubbed in backend integration tests.

## Orchestrator tests

Cover:

1. Workflow starts and interrupts.
2. Interrupt payload contains expected decision.
3. Workflow resumes using the same thread ID.
4. Product Brief contains selected option and founder comment.
5. Unknown thread ID returns a clear error.

## Smoke test

Create `scripts/smoke-test.sh` that:

1. Creates Tradigo.
2. Extracts decision ID.
3. Resolves decision.
4. Confirms project status is `COMPLETED`.
5. Confirms artifact type is `PRODUCT_BRIEF`.

Use `curl` and `jq`.

---

# Documentation Requirements

## README.md

Include:

- Project purpose
- Current milestone
- Architecture overview
- Local prerequisites
- Startup commands
- Smoke test command
- Known limitations

## docs/architecture.md

Include:

- Component diagram
- Business ownership boundaries
- Workflow start sequence
- Workflow resume sequence
- Persistence strategy
- Why Slack and LLM calls are deliberately excluded from Milestone 1

## docs/milestones.md

Document:

```text
M1: HTTP Idea → Decision → Product Brief
M2: Slack integration
M3: LLM-powered Brainstorm and Product agents
M4: GitHub and engineering agents
M5: Founder Inbox and proactive notifications
M6: WhatsApp escalation channel
```

---

# AGENTS.md

Create repository instructions for coding agents:

```markdown
# FounderOS Agent Instructions

## Objective
Build FounderOS incrementally. Prioritise durable state, auditability, and deterministic workflows before autonomy.

## Rules
- Do not add features outside the active milestone.
- Do not add Slack, WhatsApp, Sub2API, Redis, Kafka, or frontend code in Milestone 1.
- Keep Spring Boot as the owner of business records.
- Keep LangGraph responsible for workflow execution and checkpoints.
- Every state transition must be explicit and testable.
- Never rely only on model output to declare success.
- All externally visible behavior must have tests.
- Use UUIDs for entity IDs.
- Use UTC timestamps in persistence.
- Do not commit secrets.

## Completion
Milestone 1 is complete only when `scripts/smoke-test.sh` passes from a clean local setup.
```

---

# Implementation Order

Execute in this order:

## Step 1 — Repository bootstrap

- Create all directories and root files.
- Create `.gitignore` for Java, Python, IDE, environment, and generated files.
- Add initial README and AGENTS instructions.

## Step 2 — PostgreSQL

- Add Docker Compose.
- Verify PostgreSQL health.

## Step 3 — Spring Boot skeleton

- Generate Maven project.
- Add dependencies.
- Add health endpoint.
- Configure Flyway and PostgreSQL.

## Step 4 — Domain persistence

- Add migrations.
- Add entities, repositories, enums, DTOs.
- Add service-layer state transition rules.

## Step 5 — Python orchestrator skeleton

- Add FastAPI.
- Add LangGraph graph.
- Add PostgreSQL checkpointing.
- Add start and resume endpoints.

## Step 6 — Backend/orchestrator integration

- Add HTTP client from Spring Boot to orchestrator.
- Implement project creation flow.
- Implement decision resolution flow.

## Step 7 — Testing

- Unit tests.
- Integration tests.
- Orchestrator tests.
- Smoke test.

## Step 8 — Documentation and cleanup

- Update architecture docs.
- Verify startup from a clean checkout.
- Run all tests.
- Run smoke test.

---

# Definition of Done

Milestone 1 is complete only when all of the following are true:

- `docker compose up -d postgres` starts a healthy PostgreSQL instance.
- Spring Boot starts on port 8080.
- FastAPI orchestrator starts on port 8000.
- `POST /api/projects` creates Tradigo and returns an open founder decision.
- The workflow is persisted in a waiting state.
- `POST /api/decisions/{id}/resolve` resumes the same workflow thread.
- A Product Brief artifact is persisted.
- The project and workflow are marked `COMPLETED`.
- Duplicate resolution is rejected.
- Invalid option selection is rejected.
- Automated tests pass.
- `scripts/smoke-test.sh` passes.
- No LLM, Slack, WhatsApp, frontend, Redis, Kafka, or Sub2API code has been added.

---

# Codex Execution Instruction

Use the following instruction when starting Codex:

```text
Read AGENTS.md and FOUNDER_OS_M1.md completely before making changes.
Implement Milestone 1 exactly as specified.
Work through the implementation order without adding later-milestone features.
Run tests after each meaningful phase.
Keep a running progress log in docs/progress.md containing completed work, commands run, test results, known issues, and the exact next task.
Do not stop after scaffolding. Continue until the Definition of Done is satisfied or a genuine external blocker is reached.
When blocked, document the blocker precisely and provide the smallest actionable decision required from the founder.
```
