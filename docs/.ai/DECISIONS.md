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

## DEC-015 — M3A contract-driven mock boundary

**Status:** Implemented

M3A uses one typed frontend API client in both mock and future real modes. Mock Service Worker intercepts product API calls at the HTTP boundary, owns mutation state, and contains no Slack administration DTOs. Components do not import server fixtures. Mock mode is visibly labelled and can be disabled; M3B will replace only the reviewed organization slice.

## DEC-016 — Productive frontend vocabulary and truthful state

**Status:** Implemented

Job, Soul, Position, and Assignment remain separate in TypeScript types and product copy. The Organization canvas treats coordinates as presentation state and explicitly states that dragging cannot change authority. Content activity, evidence conflicts, failed checks, and model/tool absence are shown truthfully rather than simulated as agent theatre.

## DEC-017 — Character-first game feel

**Status:** Implemented by founder direction

Employees are presented as collectible characters with a portrait, archetype, style traits, status, and current quest. Employee levels are excluded because they imply capability differences. Creating an employee creates only a Soul in the available talent pool. Positions are created independently, and appointing available talent to a vacant Position creates the Assignment. Game presentation may increase attachment and clarity, but it cannot imply capabilities, work, or authority that do not exist.

## DEC-018 — Talent, Position, and appointment are separate lifecycles

**Status:** Implemented by founder direction

A Soul may exist without a Position. A Position may remain vacant without a Soul. Only an explicit appointment command connects them through an Assignment. The Talent Library owns character creation and bench visibility; Organization Studio owns vacancy context and initiates appointment from available talent.

## DEC-019 — Soul cannot affect professional competence or output quality

**Status:** Approved by founder

FounderOS is a productivity system first. Job Definition and Task Contract supply professional competence, required execution method, tools, validators, evidence rules, output schema, completion conditions, and quality thresholds. Every Soul appointed to the same Position must be equally capable of satisfying those requirements.

Soul is limited to identity, visual character, voice, communication style, and preferences among equally valid non-material alternatives. It cannot skip steps, weaken research or fact checking, change calculations, reduce review, alter permissions, bypass approval, or trade correctness for personality. The product must not expose employee levels, and it must not represent Soul style traits as intelligence, speed, reliability, quality, or other capability scores.

Required runtime invariant:

```text
Same Job + Same Task Contract + Different Soul
=> identical competence, workflow, evidence, validation, authority, and quality floor
=> only compliant presentation style may differ
```

## DEC-020 — Project memory survives reassignment

**Status:** Approved by founder

Authoritative work memory belongs to Project, Position, WorkTask, WorkflowRun, FounderDecision, Artifact, Evidence, Activity, and checkpoint records; it never belongs exclusively to the currently appointed Soul. WorkTask accountability is stable through `accountablePositionId`, while `currentAssignmentId` identifies the executing Soul and Assignment history preserves attribution over time.

Reassignment is an explicit, transactional lifecycle operation. It ends the previous Assignment, records an immutable handoff snapshot, creates the new Assignment, and emits an auditable reassignment event. The handoff snapshot contains current objective, completed work, active work, remaining actions, blockers, risks, decisions, Artifact and Evidence references, workflow thread/checkpoint, and the exact next action. It is a readable index, not a replacement for authoritative records.

The new Agent Instance reconstructs context from the Project, Position playbook, Task Contract, authoritative records, and the existing workflow checkpoint, then resumes from the same durable work state. Relevant work knowledge must be attached to Project or Task before an Assignment ends. Soul-specific relationship or communication preferences may follow the Soul but cannot change project facts, progress, or quality requirements.
