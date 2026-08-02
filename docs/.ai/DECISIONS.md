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

## DEC-009 — Slack is the FounderOS team workspace

**Status:** Approved

M2 presents FounderOS as a small team inside Slack rather than as a single approval-notification bot. The experience includes functional and project channels, direct interaction with named agent roles, and a Chief of Staff who serves as the founder's default coordinator.

## DEC-010 — Core M2 agent roster

**Status:** Approved

M2 starts with Chief of Staff, Product Lead, Research Analyst, Engineering Lead, and Growth Lead. Each task has exactly one accountable agent. Additional agents and unrestricted delegation are deferred until the core interaction and authority model is proven.

## DEC-011 — Durable Slack team semantics

**Status:** Approved

Slack is a human-facing transport and workspace, not the source of truth. Spring Boot owns conversations, tasks, assignments, decisions, messages, and artifacts. Slack processing must be verified, authorized, asynchronous, restart-safe, and idempotent. The orchestrator neither owns business history nor sends Slack messages directly.

## DEC-012 — Single Slack App with explicit personas

**Status:** Implemented

One installed FounderOS bot represents all M2 agents. Each outbound message visibly names its accountable persona. Direct messages select a specialist with an explicit English or Chinese role prefix; an unaddressed DM routes to Chief of Staff. Multiple bot installations and misleading Slack authorship are rejected.

## DEC-013 — PostgreSQL inbox and outbox

**Status:** Implemented

Slack ingress persists a unique event before acknowledgement. Separate database-backed workers route inbound work and deliver outbound `chat.postMessage` or `chat.update` operations. This meets M2 restart and retry requirements without introducing Redis or Kafka.

## DEC-014 — Least-privilege invited-channel Slack access

**Status:** Implemented

The app requests only `app_mentions:read`, `channels:history`, `groups:history`, `im:history`, and `chat:write`. It does not request `chat:write.public`; the founder must explicitly invite FounderOS to working channels and then bind their immutable Slack IDs in FounderOS.
