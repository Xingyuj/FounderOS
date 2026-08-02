# Durable Decisions

## DEC-001 — Controlled M1 scope

**Status:** Approved

Milestone 1 contains only the HTTP-based Idea -> Decision -> Product Brief workflow. Slack, WhatsApp, frontend, multiple agents, Sub2API, autonomous coding, and deployment are excluded.

## DEC-002 — Backend owns business records

**Status:** Approved

Spring Boot is authoritative for Project, FounderDecision, Artifact, and WorkflowRun. LangGraph checkpoint data is execution state and must not replace business records.

## DEC-003 — Python orchestrator

**Status:** Approved

Use Python/FastAPI/LangGraph for orchestration and Java/Spring Boot for the core platform. This preserves the founder's Java strengths while using the more mature Python agent ecosystem.

## DEC-004 — Durable checkpointing

**Status:** Approved

The final M1 implementation must use PostgreSQL-backed workflow persistence. In-memory checkpointing is permitted only during an intermediate spike and must not remain as the completed solution.

## DEC-005 — No model dependency in M1

**Status:** Approved

The founder question and Product Brief may initially be generated deterministically. The milestone validates workflow state and human approval, not LLM quality.

## DEC-006 — Dedicated checkpoint schema

**Status:** Implemented

LangGraph checkpoint tables live in the PostgreSQL `langgraph` schema. Flyway and all authoritative Spring business records remain in `public`, preventing checkpoint setup from interfering with Flyway's empty-schema migration guarantee.

## DEC-007 — Stable backend-generated thread IDs

**Status:** Implemented

The backend generates `project-<project UUID>` and persists it on Project, FounderDecision, and WorkflowRun. Start/resume responses must return the same thread ID or the business run is marked failed.

## DEC-008 — Synchronous M1 failure semantics

**Status:** Implemented

M1 uses synchronous backend-to-orchestrator HTTP. If start or resume fails, Project and WorkflowRun are marked FAILED with an audit message; automatic reconciliation is deferred. Java's simple HTTP/1.1 request factory is used for predictable FastAPI request-body delivery.
