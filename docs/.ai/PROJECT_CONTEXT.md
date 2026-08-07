# FounderOS Project Context

## Vision

FounderOS is a human-in-the-loop operating system for a one-person company. A founder supplies ideas and strategic decisions; autonomous agents research, design, implement, review, and test approved work. The system must pause at material decisions, notify the founder, persist all state, and resume reliably.

## Current milestone

Milestone 3A: Frontend Experience Discovery.

M1 and local M2 are complete. M3A validates the Command Center, Content Studio, Organization Studio, product vocabulary, and typed mock contracts before the first organization persistence migration. Real Slack workspace verification remains an independent external integration task.

## Implemented state

Milestone 1 is implemented as a Spring Boot public API and a FastAPI/LangGraph internal orchestrator. M2 adds durable Slack inbox/outbox routing. M3A adds a React/TypeScript frontend using typed product contracts and MSW scenarios; it deliberately does not change backend authority or persistence. Automated frontend component, decision, content-inspection, production-build, and browser journeys pass.

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
- React, TypeScript, Vite, TanStack Query, React Flow, MSW, Vitest, and Playwright

## Architectural intent

Spring Boot owns durable business records and public APIs. Python/LangGraph owns workflow execution and checkpoints. PostgreSQL is the durable store. The implementation should remain small enough to replace or extend individual pieces later.

Job Definition and Task Contract own professional competence and output quality. Soul is limited to identity and compliant presentation style. Appointing a different Soul must never weaken workflow steps, tools, evidence, validation, permissions, approvals, or quality thresholds.

## Deferred capabilities

- Slack and WhatsApp interaction
- Founder decision inbox UI
- Multiple specialist agents
- Sub2API and multi-provider model routing
- GitHub automation
- Autonomous code execution
- Deployment and monitoring
