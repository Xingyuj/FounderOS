# FounderOS Milestone 2 — Slack Team Experience

## 1. Objective

Turn Slack into the human-facing workspace for FounderOS. The founder should feel as though they are collaborating with a small, clearly structured human team: several functional channels, direct messages with individual agents, and a Chief of Staff who acts as the default entry point and coordinates work across the team.

Milestone 2 proves the team interaction model and durable Slack control flow. It does not attempt to make every agent independently intelligent. Deterministic behavior, fixtures, or constrained model-assisted responses are acceptable where necessary to validate routing, persistence, permissions, and founder approval.

The target experience is:

```text
Founder speaks in a channel or direct message
        ↓
FounderOS verifies the Slack workspace and user
        ↓
The message is persisted and routed to one accountable agent
        ↓
The agent acknowledges or performs a bounded unit of work
        ↓
The Chief of Staff coordinates cross-functional work
        ↓
Material decisions pause and return to the founder for approval
        ↓
The decision, work history, and resulting artifacts remain durable
```

## 2. Product Principles

1. **Slack is the workspace, not the source of truth.** Slack presents conversations and controls; Spring Boot remains authoritative for projects, tasks, decisions, agent assignments, messages, and artifacts.
2. **One accountable owner per task.** Multiple agents may contribute, but every task has exactly one responsible agent.
3. **The Chief of Staff is the default coordinator.** It triages ambiguous requests, delegates work, summarizes progress, surfaces conflicts, and escalates founder decisions. It does not silently make material founder decisions.
4. **Conversation must correspond to durable work.** A formal task, decision, assignment, or artifact created through Slack must be persisted outside Slack.
5. **Agents communicate with purpose.** Do not generate theatrical agent-to-agent chatter. Messages should convey a question, decision, result, risk, handoff, or meaningful progress.
6. **Human authority is explicit.** External publication, spending, deployment, destructive actions, sensitive-data access, and changes to product direction require founder approval.
7. **Start with a small team.** M2 establishes a useful core roster before adding more specialist roles.

## 3. Scope

### Included

- One allow-listed Slack workspace and one allow-listed founder user.
- Slack App installation and OAuth token storage appropriate for local/development operation.
- Request signature verification, replay protection, event deduplication, and authorization checks.
- Functional channels and project channels recognized through explicit bindings.
- Direct messages between the founder and each registered agent persona.
- A Chief of Staff persona as the default entry point and coordinator.
- A core roster of Product Lead, Research Analyst, Engineering Lead, and Growth Lead.
- Durable inbound/outbound message records and Slack thread correlation.
- Durable tasks with a single accountable agent, status, source conversation, and project association.
- Message routing by direct recipient, channel binding, mention, project context, and Chief of Staff triage.
- Founder decisions rendered in Slack and resolved through an interactive action.
- Idempotent processing of Slack retries and repeated interactive actions.
- Asynchronous processing after immediate acknowledgement to Slack.
- Operational activity separated from primary human-facing discussion.
- Automated tests and a Slack-compatible end-to-end test harness.

### Excluded

- Autonomous software implementation or GitHub write access.
- Unrestricted web browsing or arbitrary tool use by agents.
- Agent-created Slack channels without founder approval.
- Multiple founders, multiple Slack workspaces, guest users, or public shared channels.
- WhatsApp, email, a web inbox, voice, or mobile applications.
- Production-grade cloud deployment, enterprise secret management, billing, or multi-tenant administration.
- Fully autonomous multi-agent planning loops.
- Agent-to-agent conversations that do not create or advance durable work.
- A large dynamic roster or user-created agent personas.

## 4. Slack Workspace Model

### 4.1 Functional channels

The initial recommended channels are:

| Channel | Primary owner | Purpose |
| --- | --- | --- |
| `#founder-desk` | Chief of Staff | Founder priorities, daily summaries, decisions, cross-team coordination, and the default command surface. |
| `#product` | Product Lead | Product definition, user needs, roadmap, scope, and Product Brief discussion. |
| `#engineering` | Engineering Lead | Architecture, delivery plans, technical risks, verification, and engineering status. |
| `#growth` | Growth Lead | Positioning, launch plans, content proposals, experiments, and metric reviews. |
| `#operations` | Chief of Staff | Process, operating cadence, incidents, costs, and administrative work. |
| `#agent-activity` | System | Concise audit-oriented activity events that would be noisy in working channels. |

Research Analyst is cross-functional. It works in the channel associated with the requesting task rather than owning a mandatory `#research` channel. A dedicated research channel may be added later if usage demonstrates a need.

Channel names are recommendations, not identifiers. FounderOS binds Slack channel IDs to channel roles so renaming a channel does not break routing.

### 4.2 Project channels

A project may be bound to a channel such as `#project-founder-os`. A project channel is the shared room for cross-functional work on that project. Functional channels retain professional context and standards; the project channel contains project-specific coordination and decisions.

M2 may recognize and bind an existing project channel. Automatic channel creation is excluded. A channel can be bound to at most one active project, and a project can have at most one primary Slack project channel in M2.

### 4.3 Direct messages

The founder can direct-message any registered agent. A DM establishes the requested agent as the initial owner unless:

- the requested work is outside that agent's responsibility;
- the request requires another agent's restricted capability;
- it is a material founder decision rather than a task; or
- delegation would create an unauthorized external action.

If reassignment is needed, the contacted agent explains the handoff and the new accountable owner. Formal work originating in a DM must still be persisted and attached to a project or explicitly recorded as unassigned inbox work.

Because one Slack App normally presents one bot identity, M2 must not pretend that several independent Slack bot users exist unless the selected Slack installation model explicitly supports and authorizes that configuration. Agent identity must always be visible in message content or blocks, for example `Product Lead · FounderOS`, while one installed FounderOS app may deliver the messages.

## 5. Core Agent Roster

### 5.1 Chief of Staff

The Chief of Staff is the founder's default interface and the coordinator for ambiguous or cross-functional work.

Responsibilities:

- triage founder messages that lack an explicit owner;
- create or locate the relevant project and task;
- assign exactly one accountable agent;
- coordinate bounded contributions from other agents;
- maintain priorities and surface blocked work;
- consolidate duplicate or conflicting updates;
- provide daily or requested summaries;
- convert material questions into auditable Founder Decisions;
- ensure approval exists before restricted actions.

The Chief of Staff may recommend a decision but must not resolve a Founder Decision on the founder's behalf.

### 5.2 Product Lead

- Clarifies customer, problem, scope, requirements, and success criteria.
- Owns Product Briefs and product-definition tasks.
- Requests research or engineering input where needed.
- Escalates material changes to audience, value proposition, scope, or roadmap.

### 5.3 Research Analyst

- Conducts bounded market, customer, competitor, and factual research.
- Separates evidence, assumptions, and recommendations.
- Records sources when external research is introduced in a later milestone.
- Does not make final product or investment decisions.

### 5.4 Engineering Lead

- Produces architecture proposals, delivery plans, technical risk analysis, and verification reports.
- Owns engineering tasks and technical artifacts.
- Must request approval before deployment, destructive operations, security weakening, paid infrastructure, or difficult-to-reverse architecture changes.
- GitHub write operations and autonomous coding remain outside M2.

### 5.5 Growth Lead

- Develops positioning, launch plans, content drafts, channel experiments, and measurement plans.
- May prepare external-facing material but cannot publish, send, purchase, or launch without founder approval.
- Escalates brand, budget, audience, and public-commitment decisions.

## 6. Identity and Message Presentation

Every agent-authored message must visibly identify:

- the speaking agent;
- the related project when one exists;
- the task or decision when the message advances formal work;
- whether the content is a recommendation, completed result, blocker, or approval request.

Recommended message heading:

```text
Product Lead · Project: FounderOS
Recommendation · Task FOS-24
```

Messages posted by the FounderOS Slack App are representations of persisted agent activity. Slack display names, icons, or message metadata must not be treated as authorization or durable identity.

## 7. Routing Contract

Routing uses the following precedence:

1. A reply in an existing FounderOS-managed thread continues the thread's persisted project, task, decision, and accountable agent context.
2. A valid interactive action routes to the referenced persisted decision or task.
3. A DM to an agent routes to that agent.
4. An explicit supported agent mention routes to that agent.
5. A bound project channel supplies project context; a supported explicit mention or existing task thread supplies ownership.
6. A bound functional channel routes to its primary owner.
7. Ambiguous messages route to the Chief of Staff for triage.

Routing must never rely solely on mutable channel names or free-form agent names. Slack team, user, channel, message timestamp, and thread timestamp identifiers are stored with the durable record.

One inbound message may create at most one root task automatically. Contributors create child work or recorded contributions under that task; they do not create uncontrolled delegation chains. M2 limits delegation depth to one Chief-of-Staff assignment plus bounded contributor requests.

## 8. Conversation and Work States

### 8.1 Task

Minimum task fields:

```text
id                  UUID
projectId           UUID nullable
title               VARCHAR(255)
description         TEXT
status              NEW | ASSIGNED | IN_PROGRESS | WAITING_FOR_FOUNDER | BLOCKED | COMPLETED | CANCELLED | FAILED
accountableAgent    CHIEF_OF_STAFF | PRODUCT_LEAD | RESEARCH_ANALYST | ENGINEERING_LEAD | GROWTH_LEAD
createdBy            FOUNDER | AGENT | SYSTEM
sourceConversationId UUID nullable
parentTaskId         UUID nullable
createdAt            TIMESTAMPTZ
updatedAt            TIMESTAMPTZ
completedAt          TIMESTAMPTZ nullable
```

Transitions must be validated. `WAITING_FOR_FOUNDER` requires an open Founder Decision. `COMPLETED` requires a recorded result, artifact, or explicit completion note.

### 8.2 Slack conversation

Minimum conversation fields:

```text
id                  UUID
slackTeamId         VARCHAR
slackChannelId      VARCHAR
slackThreadTs       VARCHAR nullable
kind                DM | FUNCTIONAL_CHANNEL | PROJECT_CHANNEL
projectId           UUID nullable
taskId              UUID nullable
agentRole           VARCHAR nullable
createdAt           TIMESTAMPTZ
updatedAt           TIMESTAMPTZ
```

### 8.3 Slack message delivery

Inbound event processing and outbound delivery must be auditable. Store at least:

- Slack event or payload identifier;
- team, user, channel, message timestamp, and thread timestamp;
- direction and event type;
- related project, task, decision, and agent where applicable;
- processing state and attempt count;
- sanitized error information;
- created, processed, and delivered timestamps.

Raw payload retention should be minimized. Secrets and authorization headers must never be stored in message records or logs.

## 9. Founder Decisions in Slack

Existing `FounderDecision` remains the authoritative approval record. Slack adds a presentation and response mechanism.

A decision message contains:

- the responsible agent;
- the project and task;
- the question and necessary context;
- a recommendation when available;
- explicit options;
- an optional path for founder comment;
- a durable decision reference.

Interactive actions carry an opaque signed or server-resolved reference, not authoritative decision content. On selection, the backend must:

1. verify Slack signature and timestamp freshness;
2. verify the workspace and founder user;
3. load the authoritative open decision;
4. verify the selected option against stored options;
5. atomically record the resolution or return the existing result;
6. enqueue workflow resume;
7. update the Slack message with the recorded outcome.

Repeated delivery or clicking must not create duplicate resolutions, workflow resumes, tasks, or artifacts.

## 10. Security Model

### 10.1 Trust boundary

Slack is an external transport. No Slack payload is trusted until verified. The Spring Boot backend remains the authorization and business-policy boundary; the orchestrator is not exposed directly to Slack.

### 10.2 Required controls

- Verify Slack request signatures using the exact raw request body.
- Reject requests outside the configured timestamp tolerance to prevent replay.
- Allow only the configured Slack team ID.
- Allow founder-authority actions only from the configured founder Slack user ID.
- Ignore or explicitly reject messages from other bots, including the FounderOS bot itself, to prevent loops.
- Deduplicate Events API deliveries and interactive payloads using stable external identifiers plus payload-type-specific uniqueness constraints.
- Acknowledge Slack within its required response window and process durable work asynchronously.
- Store Slack signing secrets and tokens outside Git; redact them from logs and error responses.
- Use least-privilege Slack OAuth scopes and document every requested scope.
- Do not expose internal workflow endpoints publicly as Slack endpoints.
- Escape or safely render untrusted text in Slack blocks and logs.
- Record security-relevant rejection events without storing secrets or excessive raw content.

### 10.3 Authorization levels

M2 recognizes:

- **Founder:** may create work, change priority, resolve decisions, cancel work, and approve restricted actions.
- **Allowed participant:** excluded from M2. Messages from non-founder humans may be recorded as unauthorized events but must not advance work.
- **FounderOS agent/system:** may propose, assign within policy, update status, and create approval requests; it cannot grant founder authority to itself.

## 11. Asynchronous Processing and Reliability

Slack event and interaction endpoints must verify, persist, and acknowledge quickly. Workflow execution and message generation occur after acknowledgement.

Recommended flow:

```text
Slack request
  -> verify signature and authorization
  -> insert inbox event with unique external key
  -> acknowledge
  -> worker claims event
  -> route and update authoritative business state
  -> insert outbound message
  -> delivery worker posts to Slack
  -> persist Slack timestamp and delivery result
```

M2 may implement a PostgreSQL-backed inbox/outbox worker rather than introduce Kafka or Redis. Claiming and retry behavior must survive application restart. Retries use bounded exponential backoff and distinguish transient delivery failures from permanent authorization or validation failures.

An event is not considered processed merely because Slack was acknowledged. An outbound message is not considered delivered until Slack returns a successful response and its identifier is stored.

## 12. API Boundary

Public founder HTTP APIs from M1 remain supported. Slack-specific inbound endpoints belong to Spring Boot, for example:

```text
POST /integrations/slack/events
POST /integrations/slack/interactions
POST /integrations/slack/commands        # only if a command is included in the final UX
```

Exact endpoint names may change during implementation, but all Slack ingress must pass through the backend security and persistence boundary.

Internal orchestration contracts may be extended for task routing and agent execution. The backend provides authoritative IDs and policy context; the orchestrator returns proposed messages, decisions, task updates, and artifacts. The orchestrator must not write Slack directly in M2.

## 13. Required User Journeys

### Journey A — Chief of Staff triage

1. Founder posts an ambiguous request in `#founder-desk`.
2. FounderOS verifies and persists the event.
3. Chief of Staff associates it with a project or asks one concise clarifying question.
4. A durable task is created and assigned to one agent.
5. The founder sees an acknowledgement containing project, owner, and next action.

### Journey B — Functional channel work

1. Founder asks for a revised product scope in `#product`.
2. The Product Lead becomes accountable based on the channel binding.
3. Work continues in a Slack thread correlated to the task.
4. The result is persisted as an artifact or task result.
5. The Product Lead posts a concise completion summary.

### Journey C — Direct message

1. Founder sends a DM addressed to the Engineering Lead persona.
2. The request is persisted and assigned to Engineering Lead.
3. If product input is required, Engineering Lead requests one bounded contribution while retaining accountability.
4. Founder receives the consolidated answer in the original DM thread.

### Journey D — Cross-functional coordination

1. Founder starts work in a bound project channel.
2. Chief of Staff assigns one accountable owner and records contributors.
3. Contributors provide results without opening unbounded agent discussions.
4. Chief of Staff posts one consolidated status update and records any conflicts.

### Journey E — Founder approval

1. An agent encounters a material decision.
2. A Founder Decision is persisted before its Slack message is sent.
3. Founder selects a stored option and optionally adds a comment.
4. The response is recorded exactly once and the workflow resumes asynchronously.
5. The original message shows the final selection and downstream completion status.

### Journey F — Restart and retry

1. A verified Slack event is persisted.
2. The backend stops before routing or outbound delivery completes.
3. After restart, the worker safely resumes processing.
4. Exactly one task and the intended outbound message exist.

## 14. Acceptance Criteria

Milestone 2 is complete only when:

1. The configured founder can interact through `#founder-desk`, at least two functional channels, one project channel, and an agent-addressed DM flow.
2. Chief of Staff correctly triages an ambiguous request and assigns exactly one accountable agent.
3. Product Lead, Research Analyst, Engineering Lead, and Growth Lead have registered identities and enforceable responsibility boundaries.
4. Channel bindings use Slack IDs and survive channel renaming.
5. Formal work from Slack creates durable conversations, messages, and tasks linked to existing business records where relevant.
6. One material question is presented and resolved through Slack using the existing Founder Decision authority model.
7. Invalid signatures, stale timestamps, wrong workspaces, non-founder approvals, bot loops, invalid options, duplicate events, and duplicate clicks are rejected or handled idempotently.
8. Slack endpoints acknowledge promptly while work continues asynchronously.
9. A persisted inbound event and pending outbound message survive process restart and complete without duplicate work.
10. Slack/API failure paths leave an auditable state and support bounded retry where safe.
11. The orchestrator does not become authoritative for business data and does not hold Slack delivery credentials.
12. Automated backend and orchestrator tests pass, and an end-to-end Slack-compatible smoke test demonstrates the required journeys.
13. Documentation lists the exact OAuth scopes, setup steps, test evidence, limitations, and recovery procedure.

## 15. Implementation Sequence

1. Create migrations and backend domain records for agent registry, channel binding, conversation, message/event, task, assignment/contribution, and inbox/outbox processing.
2. Implement Slack signature verification, workspace/user authorization, replay protection, bot filtering, and idempotent ingress.
3. Implement PostgreSQL-backed asynchronous inbox/outbox workers and restart-safe retry behavior.
4. Register the core agent roster and implement deterministic routing precedence.
5. Implement Chief of Staff triage and task ownership rules.
6. Render agent identity consistently in channels and DM conversations.
7. Connect Slack interactive decisions to the existing Founder Decision resolution path.
8. Add integration tests, Slack API fixtures/fakes, negative security tests, restart proof, and the end-to-end smoke test.
9. Document Slack App configuration, scopes, local setup, operations, and recovery.

## 16. Deferred Decisions for Implementation Planning

The following choices may be made as reversible implementation details, but must be documented before Slack App configuration is finalized:

- whether agent-addressed DMs use App Home messages, a shared FounderOS bot DM with explicit persona selection, or a Slack-supported multi-agent identity mechanism;
- whether M2 includes one slash command or relies entirely on messages, mentions, and interactive blocks;
- the exact least-privilege OAuth scope list after the selected Slack interaction mechanisms are confirmed;
- worker scheduling and retry intervals;
- retention duration for sanitized Slack message content and security rejection records.

None of these decisions may weaken the authority, auditability, or security requirements in this specification.
