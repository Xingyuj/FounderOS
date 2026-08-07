# FounderOS

FounderOS is a durable, human-in-the-loop operating system for a one-person company. M1 and M2 prove durable HTTP and Slack control flow. M3A adds a frontend-first product prototype for understanding the company, resolving decisions, inspecting content production, and arranging the organization.

## Architecture

The Spring Boot service on port 8080 owns Project, FounderDecision, WorkflowRun, and Artifact records. The FastAPI/LangGraph service on port 8000 owns workflow execution and PostgreSQL checkpoints. PostgreSQL 16 stores both, with separate business and LangGraph-owned tables.

## Prerequisites

- Java 21
- Docker with Compose
- Python 3.12+
- `curl` and `jq`
- Node.js 22+ for the M3A frontend

## Run locally

```bash
cd orchestrator
python3.12 -m venv .venv
.venv/bin/pip install -e '.[test]'
cd ..
./scripts/start-local.sh
```

In another terminal:

```bash
./scripts/smoke-test.sh
```

Individual test suites:

```bash
cd backend && ./mvnw test
cd orchestrator && .venv/bin/pytest
cd frontend && npm run lint && npm run typecheck && npm test && npm run build
```

The frontend runs independently against typed mock APIs:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. See the [frontend guide](frontend/README.md) for scenarios and browser tests.

## Known limitations

Agent responses remain deterministic; specialist LLM reasoning, autonomous tools, general user authentication, and deployment configuration are deferred. Slack supports one allow-listed workspace and founder, uses environment-based credentials for local operation, and requires a public HTTPS callback for real workspace use. Backend-to-orchestrator coordination is still synchronous; Slack transport itself is isolated through restart-safe PostgreSQL inbox/outbox queues.

See [architecture](docs/architecture.md), [API](docs/api.md), [milestones](docs/milestones.md), and the [frontend roadmap](docs/FOUNDER_OS_FRONTEND_ROADMAP.md).

## Milestone 2 Slack team

M2 adds a durable Slack team surface with Chief of Staff, Product Lead, Research Analyst, Engineering Lead, and Growth Lead personas. Slack messages become persisted tasks through a verified inbox; responses and decision updates use a restart-safe outbox. See the [M2 specification](docs/FOUNDER_OS_M2.md) and [Slack setup guide](docs/slack-setup.md).
