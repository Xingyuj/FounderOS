# FounderOS Project Context

## Vision

FounderOS is a human-in-the-loop operating system for a one-person company. A founder supplies ideas and strategic decisions; autonomous agents research, design, implement, review, and test approved work. The system must pause at material decisions, notify the founder, persist all state, and resume reliably.

## Current milestone

Milestone 1: Idea -> Founder Decision -> Product Brief.

This milestone is infrastructure validation, not a complete multi-agent product. Deterministic workflow logic is acceptable. Slack, WhatsApp, model routing, autonomous engineering, and a web UI are deliberately deferred.

## Implemented state

Milestone 1 is implemented as a Spring Boot public API and a FastAPI/LangGraph internal orchestrator. PostgreSQL `public` contains Flyway-managed authoritative business tables; the `langgraph` schema contains durable execution checkpoints. The HTTP pause/resume path, negative decision cases, and process-restart recovery have been verified locally.

## Repository

Remote: `git@github.com:Xingyuj/FounderOS.git`

Expected local path: `~/workspace/FounderOS`

## Approved stack

- Java 21
- Spring Boot 3.x
- Maven
- PostgreSQL 16
- Flyway
- Testcontainers
- Python 3.12+
- FastAPI
- LangGraph
- Pydantic v2
- pytest/httpx
- Docker Compose

## Architectural intent

Spring Boot owns durable business records and public APIs. Python/LangGraph owns workflow execution and checkpoints. PostgreSQL is the durable store. The implementation should remain small enough to replace or extend individual pieces later.

## Deferred capabilities

- Slack and WhatsApp interaction
- Founder decision inbox UI
- Multiple specialist agents
- Sub2API and multi-provider model routing
- GitHub automation
- Autonomous code execution
- Deployment and monitoring
