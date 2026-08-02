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

M1 has deterministic content and intentionally has no authentication, UI, LLM integration, notifications, or deployment configuration. Backend coordination uses synchronous HTTP. If resume fails after a founder response is recorded, the run is marked failed for auditability; automatic retry/reconciliation is deferred.

See [architecture](docs/architecture.md), [API](docs/api.md), and [milestones](docs/milestones.md).

