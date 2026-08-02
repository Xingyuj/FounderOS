# FounderOS

FounderOS is a durable, human-in-the-loop operating system for a one-person company. Milestone 1 proves the HTTP path from a project idea, through a persisted founder decision and workflow interrupt, to a stored Product Brief.

## Architecture

The Spring Boot service on port 8080 owns Project, FounderDecision, WorkflowRun, and Artifact records. The FastAPI/LangGraph service on port 8000 owns workflow execution and PostgreSQL checkpoints. PostgreSQL 16 stores both, with separate business and LangGraph-owned tables.

## Prerequisites

- Java 21
- Docker with Compose
- Python 3.12+
- `curl` and `jq`

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
```

## Known limitations

Agent responses remain deterministic; specialist LLM reasoning, autonomous tools, general user authentication, and deployment configuration are deferred. Slack supports one allow-listed workspace and founder, uses environment-based credentials for local operation, and requires a public HTTPS callback for real workspace use. Backend-to-orchestrator coordination is still synchronous; Slack transport itself is isolated through restart-safe PostgreSQL inbox/outbox queues.

See [architecture](docs/architecture.md), [API](docs/api.md), and [milestones](docs/milestones.md).

## Milestone 2 Slack team

M2 adds a durable Slack team surface with Chief of Staff, Product Lead, Research Analyst, Engineering Lead, and Growth Lead personas. Slack messages become persisted tasks through a verified inbox; responses and decision updates use a restart-safe outbox. See the [M2 specification](docs/FOUNDER_OS_M2.md) and [Slack setup guide](docs/slack-setup.md).
