# FounderOS Frontend and Product Roadmap

Status: working delivery plan
Created: 2026-08-06
Scope: frontend-first product evolution after M2

## 1. Product objective

FounderOS is a productivity system for a founder to organize an AI team and produce real business outputs. Personality, visual organization, motion, and game-like feedback exist to make the system easier to understand, configure, trust, and enjoy. They are not independent product goals.

The first production vertical is content creation:

```text
Founder proposes a topic
        ↓
Content Lead chooses an angle
        ↓
Researcher collects evidence
        ↓
Writer produces a draft
        ↓
Editor reviews and revises
        ↓
Fact Checker verifies claims
        ↓
Founder approves
        ↓
FounderOS stores a publishable artifact
```

Every planned feature must improve at least one of:

- output speed;
- output quality;
- founder control;
- system explainability;
- continuity across sessions;
- the founder's willingness to return and continue real work.

## 2. Product principles

1. **Productivity before simulation.** FounderOS must produce useful artifacts, not merely simulate employees talking.
2. **Truthful activity.** The interface may animate real state changes, but must never simulate work that is not happening.
3. **Daily work before organization configuration.** The Command Center and Content Studio are daily surfaces. Organization Studio is an important but lower-frequency configuration surface.
4. **Job and Soul are separate.** A Job defines responsibility, professional competence, required method, tools, validation, authority, and evaluation. A Soul defines identity, visual character, and communication style.
5. **Personality cannot reduce competence or override governance.** Changing the appointed Soul cannot weaken required steps, tools, evidence, validation, output quality, permissions, or founder approval.
6. **The organization chart is executable configuration.** Reporting, review, delegation, and assignment relationships affect runtime routing and authority.
7. **Evidence over agent agreement.** Multiple agents agreeing is not validation. Tests, source provenance, independent checks, and founder decisions remain authoritative.
8. **Progressive disclosure.** The default view explains what is happening and what needs attention. Traces, retries, tool calls, and cost details remain available in an audit view.

## 3. Core product surfaces

### 3.1 Command Center

The default founder workspace answers:

- What is the company trying to achieve?
- What did the team complete recently?
- What is currently in progress?
- What is blocked?
- What decisions require the founder?
- What is the next milestone?

### 3.2 Content Studio

The production workspace for a content item contains:

- brief and intended audience;
- current production stage;
- accountable position and assigned character;
- research evidence and sources;
- outline, draft, review, and final versions;
- disagreements and unresolved claims;
- founder decisions;
- activity and audit history.

### 3.3 Organization Studio

The company configuration workspace contains:

- a draggable organization chart;
- a Job Library;
- a Soul Library;
- appointments and vacancies;
- reporting and review relationships;
- permissions and escalation rules;
- organization change history;
- import and export of organization templates.

## 4. Domain language

The following terms must remain distinct in product copy, frontend types, APIs, and persistence.

| Term | Meaning |
|---|---|
| Company | The founder's operating organization, constitution, goals, and policies |
| Job Definition | An installable definition of duties, authority, tools, inputs, outputs, and evaluation |
| Soul Definition | A portable identity containing personality, voice, style preferences, and visual identity; it does not contain professional competence or tool capability |
| Position | A seat in one company's organization, created from a Job Definition |
| Assignment | A Soul appointed to a Position for a period of time |
| Agent Instance | Runtime composition of Assignment, Job, Company, task contract, context, and tool grants |
| Reporting Relationship | The position responsible for management, delegation, and escalation |
| Review Relationship | The position responsible for independent validation or approval |
| Task Contract | Goal, inputs, permissions, prohibitions, budget, completion conditions, and output requirements |
| Project Memory | Durable project facts, progress, decisions, risks, evidence, artifacts, and active work references; independent of the appointed Soul |
| Position Memory | Durable playbook, standards, active responsibilities, and handoff history belonging to a Position rather than an employee |
| Handoff Snapshot | Immutable reassignment index containing current state and references to authoritative work records and workflow checkpoint |
| Artifact | A versioned business output produced by work |
| Organization Layout | Canvas coordinates and visual state; not authoritative organizational structure |

Runtime composition:

```text
Agent Instance
  = Assignment
  + Job Definition
  + Soul Definition
  + Company Constitution
  + Current Task Contract
  + Tool Grants
  + Project Context
```

Authority order:

```text
Platform safety and security
        > Company constitution
        > Job authority and restrictions
        > Task contract
        > Soul communication preferences
        > Soul voice, quirks, and catchphrases
```

Competence invariant:

```text
Same Job + Same Task Contract + Different Soul
        -> same required workflow and tool access
        -> same evidence and validation requirements
        -> same completion and quality threshold
        -> only presentation style and non-material preference may differ
```

Soul must never determine whether an Agent Instance is professionally capable of an appointed Job. Appointment provisions the complete Job competence contract regardless of Soul. Soul may influence tone, ordering of an explanation, and preference among equally valid non-material alternatives, but it cannot change factual accuracy, required research, calculations, reviews, safety checks, or output schemas. Product UI must not expose employee levels. Soul style traits and other game presentation must never imply capability, intelligence, speed, quality, or reliability differences.

Work ownership and attribution:

```text
WorkTask.accountablePositionId  = durable responsibility
WorkTask.currentAssignmentId    = current executor attribution
Assignment history              = who executed work during each period
Project / Task / Workflow state = durable memory across reassignment
```

## 5. Technical direction

### 5.1 Frontend

Create a new `frontend/` application using:

- React and TypeScript;
- Vite;
- React Router;
- TanStack Query for server state;
- React Flow for the organization canvas;
- Mock Service Worker for contract-driven mock APIs;
- Playwright for critical browser journeys;
- a lightweight component showcase or Storybook for reusable visual states.

Do not add a general frontend state framework until local interaction complexity demonstrates a need. Keep remote server state in TanStack Query and local canvas/editing state close to the relevant feature.

Suggested structure:

```text
frontend/
├── src/
│   ├── app/
│   ├── api/
│   ├── components/
│   ├── design-system/
│   ├── domain/
│   ├── features/
│   │   ├── command-center/
│   │   ├── content/
│   │   ├── decisions/
│   │   ├── jobs/
│   │   ├── organization/
│   │   ├── souls/
│   │   └── tasks/
│   ├── mocks/
│   └── test/
├── public/
└── e2e/
```

### 5.2 Organization data versus layout data

Business structure and visual layout must be persisted separately.

```text
Organization
├── positions
├── assignments
├── reporting relationships
└── review relationships

Organization Layout
├── position ID
├── x/y position
├── collapsed state
├── visual group
└── viewport preferences
```

Moving a card must not silently change reporting relationships. Changing a reporting relationship must be an explicit organization command with validation and an audit record.

### 5.3 Backend evolution

Keep the existing Spring Boot authority over business state and the Python orchestrator authority over workflow execution. Preserve Project, FounderDecision, WorkflowRun, Artifact, WorkTask, PostgreSQL durability, LangGraph checkpoints, and Slack inbox/outbox behavior.

Replace the fixed role model gradually:

```text
AgentRole enum / AgentProfile
        ↓ compatibility migration
JobDefinition + Position + SoulDefinition + Assignment
```

Do not delete legacy columns in the first organization migration. Add new tables and references, backfill system jobs and default positions, move writes to the new model, prove compatibility, then remove old fields in a later migration.

### 5.4 Frontend API boundary

The frontend must consume product APIs, not Slack-specific administration DTOs. Mock and real implementations must share the same versioned contract.

Initial resource groups:

```text
/api/companies
/api/organizations
/api/jobs
/api/souls
/api/positions
/api/assignments
/api/projects
/api/tasks
/api/decisions
/api/content-items
/api/artifacts
/api/activity
```

### 5.5 Deployment boundary

Use logical frontend/backend separation without introducing operational fragmentation.

```text
Browser
  ├── /              React application
  └── /api/**        Spring Boot product APIs
                            ↓
                      PostgreSQL business state
                            ↓
                      FastAPI/LangGraph runtime
```

The first production deployment should use one public origin. Static frontend assets may be served by Spring Boot or a simple reverse proxy, while `/api/**` routes to Spring Boot. FastAPI internal workflow endpoints must not become browser-facing APIs.

Keep all services in the existing monorepo. Do not introduce micro-frontends, an independent frontend release organization, server-side rendering, or extra gateways until a demonstrated requirement exists.

### 5.6 Frontend/backend responsibility boundary

| Frontend responsibility | Backend responsibility |
|---|---|
| canvas coordinates and viewport | company and organization facts |
| drag previews and staged edits | valid Positions and Assignments |
| unsaved change sets | reporting and review relationships |
| optimistic visual feedback | permissions and tool grants |
| animation and presentation | task, decision, workflow, and artifact state |
| filters and expanded panels | validation, conflicts, and idempotency |
| local user preferences | audit history and durable activity |

A useful rule is: if a fact must still be true after a refresh, process restart, or login from another device, it belongs to an authoritative backend resource unless it is explicitly a personal presentation preference.

### 5.7 Experience-led vertical-slice process

Frontend-first does not mean completing the entire frontend before backend integration. Each major capability follows this loop:

```text
1. prototype the founder experience
2. test it with realistic mock states
3. convert interactions into explicit commands and queries
4. review and freeze the smallest API contract
5. implement backend invariants and persistence
6. replace that feature's mock handlers with real APIs
7. prove the journey in a browser and through service restart
8. continue to the next capability
```

The frontend may lead by one validated slice. It must not lead by an entire product roadmap.

### 5.8 Mock API discipline and exit criteria

- Components must call the same API client under mock and real modes.
- Mock Service Worker owns mock transport behavior; feature components must not import fixture records directly.
- Mocks must cover success, validation failure, conflict, authorization failure, unavailable runtime, partial completion, and waiting states.
- Consequential mutations must use explicit commands rather than replacing an entire aggregate JSON document.
- Frontend and backend DTOs must be checked with OpenAPI/JSON Schema or equivalent contract tests.
- A mock handler is removed when its real endpoint passes contract, integration, and browser tests.
- A milestone cannot be marked integrated while its primary journey silently falls back to mocks.
- Mock-only UI may be retained as a clearly labeled design scenario, never as production state.

### 5.9 Project memory and reassignment continuity

Project progress must survive employee reassignment without relying on the previous Soul's context window or private notes. Authoritative memory is assembled from Project, Position, WorkTask, Task Contract, FounderDecision, Artifact, Evidence, Activity, WorkflowRun, and LangGraph checkpoint records.

Tasks remain accountable to a Position across personnel changes. The active Assignment identifies the current executor, and Assignment history preserves who performed each action and produced each output. Reassignment must atomically:

1. close the previous Assignment with `endedAt`;
2. create an immutable Handoff Snapshot;
3. create the replacement Assignment;
4. emit an append-only reassignment event;
5. reconstruct the Agent Instance from authoritative state;
6. resume the same task and workflow checkpoint without duplicating work.

The Handoff Snapshot includes objective, completed work, current work, remaining actions, blockers, risks, material decisions, Artifact and Evidence references, workflow thread/checkpoint, and exact next action. It is never the sole source of truth. Relevant work knowledge must be promoted to Project, Position, or Task memory before the outgoing Assignment ends. Soul memory is limited to relationship and communication preferences and cannot own project facts or progress.

## 6. Roadmap overview

| Milestone | Outcome | Primary proof |
|---|---|---|
| M3A | Frontend experience discovery | A founder can understand and operate a realistic mocked company |
| M3B | Organization vertical slice | A founder can create a Position and appoint a Soul through the real persisted system |
| M3C | Organization governance and runtime migration | Advanced organization changes persist, are audited, and affect real task routing |
| M4A | Soul Studio | Personalities are portable, appointable, governed assets |
| M4B | Content production vertical | A real topic becomes an evidence-backed, reviewed artifact |
| M5 | Job, Soul, and Team packages | Assets can be safely exported, imported, and reused |
| M6 | Live feedback and product polish | Real work feels alive without becoming a game or hiding system truth |

## 7. Milestone M3A — Frontend Experience Discovery

### Objective

Validate the core experience before changing backend domain models. This is the only milestone whose primary journeys run entirely against typed mock APIs. Its output is a reviewed interaction model and the smallest contract candidate for M3B, not a production-complete frontend.

### Epic M3A-E1 — Frontend application foundation

#### Story M3A-E1-S1 — Bootstrap the application

As a developer, I can run the frontend locally with one documented command.

Acceptance criteria:

- React and TypeScript application exists under `frontend/`.
- Lint, type-check, unit-test, build, and local-start commands are documented.
- Environment configuration has safe local defaults and contains no secrets.
- Backend and orchestrator builds remain unchanged.

Suggested commits:

1. `feat(frontend): bootstrap React TypeScript application`
2. `build(frontend): add lint typecheck test and build commands`
3. `docs(frontend): document local frontend development`

#### Story M3A-E1-S2 — Establish the application shell

As a founder, I can move between Command Center, Content Studio, and Organization Studio without losing context.

Acceptance criteria:

- Persistent navigation and company switcher placeholder exist.
- Empty, loading, error, and unavailable states are designed.
- Keyboard focus and responsive desktop layout work correctly.

Suggested commits:

1. `feat(frontend): add application shell and primary navigation`
2. `feat(frontend): add shared loading empty and error states`

### Epic M3A-E2 — Visual language and product personality

#### Story M3A-E2-S1 — Create foundational design tokens

As a product team, we can express a warm, human, productive interface consistently.

Acceptance criteria:

- Color, typography, spacing, elevation, motion, and status tokens exist.
- Productive states are distinguishable without relying on color alone.
- Motion respects reduced-motion preferences.
- The style avoids both enterprise dashboard sterility and game HUD excess.

Suggested commits:

1. `feat(design): add FounderOS visual tokens and themes`
2. `feat(design): add accessible status and motion primitives`

#### Story M3A-E2-S2 — Build core product components

Components include:

- agent portrait and status;
- position card;
- task card;
- decision card;
- artifact card;
- evidence card;
- milestone progress;
- activity timeline entry.

Acceptance criteria:

- Each component has documented states and representative fixtures.
- Components do not fetch data directly.
- Components support keyboard navigation where interactive.

Suggested commits:

1. `feat(design): add agent position and task components`
2. `feat(design): add decision artifact and evidence components`
3. `test(design): cover core component states`

### Epic M3A-E3 — Contract-driven mock environment

#### Story M3A-E3-S1 — Define frontend domain contracts

As a frontend developer, I can build against stable product concepts without depending on the legacy AgentRole enum.

Acceptance criteria:

- TypeScript types represent Company, Job, Soul, Position, Assignment, Task, Decision, ContentItem, Artifact, and Activity.
- IDs and timestamps use the same representations expected from the backend.
- Types do not expose Slack transport concepts.

Suggested commits:

1. `feat(frontend): define organization and identity contracts`
2. `feat(frontend): define content task decision and artifact contracts`

#### Story M3A-E3-S2 — Create realistic mock scenarios

Required scenarios:

- healthy team actively producing content;
- vacant critical position;
- blocked content item;
- open founder decision;
- conflicting research evidence;
- failed fact check;
- completed milestone.

Acceptance criteria:

- Requests are intercepted at the HTTP boundary rather than imported as component constants.
- Scenarios can be selected deterministically for tests and demonstrations.
- Mock mutations update subsequent mock reads.

Suggested commits:

1. `test(frontend): add typed mock API and company fixtures`

### Epic M3A-E4 — Command Center prototype

#### Story M3A-E4-S1 — Show company operating state

As a founder, I can understand the company state within ten seconds.

Acceptance criteria:

- Mission, active milestone, team activity, work in progress, blockers, and recent output are visible.
- The page prioritizes actionable information over statistics.
- No activity is shown unless supported by mock or real state.

#### Story M3A-E4-S2 — Resolve a founder decision

As a founder, I can inspect context and resolve an open decision without reading an execution trace.

Acceptance criteria:

- Options, recommendation, evidence, impact, and optional comment are shown.
- Resolution requires confirmation when it changes active work.
- Resolved state updates the page immediately and remains visible in history.

Suggested commits:

1. `feat(command-center): add company status and active work`
2. `feat(command-center): add founder inbox and decision flow`
3. `test(command-center): cover daily founder journey`

### Epic M3A-E5 — Content Studio prototype

#### Story M3A-E5-S1 — Inspect content production state

As a founder, I can follow a content item from brief to final artifact.

Acceptance criteria:

- Production stage, accountable assignment, evidence, versions, reviews, and blockers are visible.
- Founder can switch between productive summary and audit detail.
- The latest artifact version is clearly distinguished from historical versions.

Suggested commits:

1. `feat(content): add content pipeline and workspace prototype`
2. `feat(content): add evidence version and review panels`
3. `test(content): cover content inspection journey`

### M3A release gate

- A founder can complete the scripted scenario without backend services.
- A first-time user distinguishes Job, Soul, Position, and Assignment.
- A founder can find and resolve an open decision.
- A founder can locate the latest content artifact and its evidence.
- One moderated product walkthrough has been completed and findings recorded.
- No backend domain migration begins until the product vocabulary and mock contracts are reviewed.
- Unvalidated secondary screens do not continue expanding while the first Organization vertical slice remains mock-only.

## 8. Milestone M3B — Organization Vertical Slice

### Objective

Turn the validated M3A interaction into the first real end-to-end product slice. M3B is intentionally narrow: render an organization, create a Position, and appoint a Soul through a persisted API. Advanced reorganization remains in M3C.

### Epic M3B-E1 — Organization canvas prototype

#### Story M3B-E1-S1 — Render and arrange the organization

As a founder, I can see Positions, assigned Souls, vacancies, and reporting relationships, and arrange the canvas for readability.

Acceptance criteria:

- Nodes are custom Position cards.
- Reporting edges are visually distinct from future review edges.
- Zoom, pan, fit-to-view, keyboard selection, and focus states work.
- Canvas coordinates are stored separately from reporting structure.
- Moving a card cannot change company authority.

Suggested commits:

1. `feat(organization): add interactive organization canvas`
2. `feat(organization): add custom position nodes and reporting edges`
3. `test(organization): cover canvas navigation and layout isolation`

### Epic M3B-E2 — Minimal contract freeze

#### Story M3B-E2-S1 — Convert prototype actions into API operations

Required first-slice operations:

```text
GetOrganization
ListJobDefinitions
ListSoulDefinitions
CreatePosition
AppointSoulToPosition
UpdateOrganizationLayout
```

Acceptance criteria:

- Each operation has request, response, validation error, conflict, and unavailable-state examples.
- Frontend types are generated from or checked against the contract.
- CreatePosition and AppointSoul are commands, not whole-organization replacements.
- The contract is reviewed before persistence work starts.

Suggested commits:

1. `docs(org-api): specify first organization vertical contract`
2. `test(contract): validate frontend organization DTOs`

### Epic M3B-E3 — Minimal organization domain

#### Stories

- M3B-E3-S1: Persist one Company and its constitution placeholder.
- M3B-E3-S2: Persist JobDefinition and SoulDefinition.
- M3B-E3-S3: Persist Position, Assignment, and OrganizationLayout.
- M3B-E3-S4: Seed a default content team compatible with M2 roles.

Acceptance criteria:

- Foreign keys and company-level uniqueness constraints are explicit.
- A Position may be vacant.
- A Soul cannot expand Job authority or tool permissions.
- One Soul cannot hold two exclusive active assignments in the same company.
- No applied Flyway migration is edited.
- Legacy M2 role records remain readable.

Suggested commits:

1. `feat(org-domain): add company job and Soul persistence`
2. `feat(org-domain): add position assignment and layout persistence`
3. `feat(org-migration): seed default organization from M2 roles`
4. `test(org-domain): prove first-slice organization constraints`

### Epic M3B-E4 — Minimal organization APIs

#### Stories

- M3B-E4-S1: Read the complete organization vertical-slice view.
- M3B-E4-S2: Create a vacant Position from a JobDefinition.
- M3B-E4-S3: Appoint a Soul to a vacant Position.
- M3B-E4-S4: Persist personal canvas layout independently.

Acceptance criteria:

- APIs use DTOs and RFC 7807 errors.
- Consequential commands accept an idempotency key or equivalent duplicate protection.
- Stale organization versions return a conflict instead of overwriting newer state.
- Appointment validation is authoritative on the backend.

Suggested commits:

1. `feat(org-api): expose organization jobs and Souls`
2. `feat(org-api): add position creation and Soul appointment commands`
3. `feat(org-api): persist independent organization layout`
4. `test(org-api): cover validation conflicts and idempotency`

### Epic M3B-E5 — Replace the first mocks

#### Stories

- M3B-E5-S1: Read organization, Job, and Soul data from real APIs.
- M3B-E5-S2: Create a Position and appoint a Soul through real commands.
- M3B-E5-S3: Recover gracefully from validation, conflict, and service errors.
- M3B-E5-S4: Remove production fallback to first-slice organization mocks.

Suggested commits:

1. `feat(frontend): connect organization read APIs`
2. `feat(frontend): connect position and appointment commands`
3. `feat(frontend): add organization conflict recovery`
4. `test(e2e): prove persisted Position and appointment journey`

### M3B release gate

- A browser reads an organization from Spring Boot.
- A founder creates a vacant Position and appoints a Soul.
- The result survives frontend refresh and full service restart.
- Canvas movement does not modify reporting structure.
- Contract, backend integration, and browser tests cover the journey.
- The primary journey has no production mock fallback.
- Existing M1 and M2 test suites remain green.

## 9. Milestone M3C — Organization Governance and Runtime Migration

### Objective

Expand the proven organization slice into a safe organization operating model and make it authoritative for task routing while preserving M1 and M2 behavior.

### Epic M3C-E1 — Position lifecycle and reporting

#### Stories

- M3C-E1-S1: Edit, duplicate, and retire a Position.
- M3C-E1-S2: Change a Position's manager through an explicit command.
- M3C-E1-S3: Reject reporting cycles and invalid cross-company relationships.
- M3C-E1-S4: Preview task, authority, and escalation impact before applying a change.

Suggested commits:

1. `feat(org-domain): add position lifecycle and reporting relationships`
2. `feat(org-api): add reporting change preview and apply commands`
3. `feat(organization): add reporting editing and impact preview`
4. `test(organization): cover cycles conflicts and destructive impacts`

### Epic M3C-E2 — Review relationships and staged changes

#### Stories

- M3C-E2-S1: Configure independent review without changing management reporting.
- M3C-E2-S2: Warn when generator and reviewer are not meaningfully independent.
- M3C-E2-S3: Stage several organization commands and review their combined impact.
- M3C-E2-S4: Undo, redo, apply, or discard the staged change set.

Acceptance criteria:

- Review edges are distinct from reporting edges.
- Self-review is rejected or explicitly governed by artifact type.
- Unsaved edits are visually distinct from active state.
- A staged change set fails atomically when any command is invalid.

Suggested commits:

1. `feat(org-domain): add governed review relationships`
2. `feat(org-api): add atomic organization change sets`
3. `feat(organization): add review edges and staged editing`
4. `test(organization): cover review independence and atomic changes`

### Epic M3C-E3 — Organization history and concurrency

#### Stories

- M3C-E3-S1: Record material changes as append-only OrganizationChange events.
- M3C-E3-S2: Show who changed what, when, and why.
- M3C-E3-S3: Detect stale organization versions.
- M3C-E3-S4: Present a comprehensible conflict recovery experience.

Suggested commits:

1. `feat(org-domain): add organization version and change audit`
2. `feat(org-api): expose organization history and conflicts`
3. `feat(organization): add audit and conflict resolution experience`
4. `test(e2e): prove concurrent organization edit handling`

### Epic M3C-E4 — Legacy task and Slack migration

#### Stories

- M3C-E4-S1: Backfill default Positions and Assignments for legacy M2 roles.
- M3C-E4-S2: Route new WorkTasks through accountable Position and Assignment.
- M3C-E4-S3: Adapt Slack channel bindings and messages without breaking delivery.
- M3C-E4-S4: Retain legacy AgentRole fields during the compatibility window.
- M3C-E4-S5: Reassign an active Position through an immutable handoff snapshot without losing task or workflow progress.

Acceptance criteria:

- Existing M1 and M2 records remain readable.
- Existing Slack integration tests continue to pass.
- New WorkTasks reference accountable Position/Assignment.
- WorkTask accountability remains attached to Position while current executor attribution follows Assignment history.
- Reassignment resumes the same workflow checkpoint and does not duplicate completed steps, decisions, messages, or artifacts.
- Handoff snapshots reference authoritative Project, Task, Decision, Artifact, Evidence, and WorkflowRun records rather than replacing them.
- No fixed AgentRole value is required by new frontend features.
- No applied Flyway migration is rewritten.

Suggested commits:

1. `feat(org-migration): complete legacy role backfill`
2. `refactor(tasks): assign work through organization positions`
3. `refactor(slack): route Slack work through organization assignments`
4. `test(migration): prove M2 compatibility and backfill behavior`

### M3C release gate

- A founder can safely reorganize a five-position content team.
- Reporting cycles, invalid self-review, stale versions, and destructive changes are handled explicitly.
- Organization changes are durable and auditable.
- Runtime task and Slack routing read the configured organization.
- Organization state and active work survive process restart.
- Existing backend, orchestrator, and Slack tests pass.

## 10. Milestone M4A — Soul Studio

### Objective

Make personality a portable, versioned, governed product asset that changes character and expression without changing professional competence, execution quality, or authority.

### Epic M4A-E1 — Soul schema and editor

#### Stories

- M4A-E1-S1: Create and edit identity, values, thinking style, social style, voice, quirks, background, and boundaries.
- M4A-E1-S2: Upload or select an avatar and visual theme.
- M4A-E1-S3: Preview introduction, disagreement, uncertainty, and founder escalation behavior.
- M4A-E1-S4: Version and duplicate a Soul.

Acceptance criteria:

- Structured form and Markdown views describe the same Soul.
- Catchphrases include frequency guidance and cannot dominate responses.
- Soul preview clearly states that it is illustrative, not a task execution.

Suggested commits:

1. `feat(souls): add Soul schema and persistence`
2. `feat(souls): add Soul editor and visual identity`
3. `feat(souls): add behavior preview and version history`
4. `test(souls): cover Soul validation and editing`

### Epic M4A-E2 — Appointment behavior and governance

#### Stories

- M4A-E2-S1: Compose Job and Soul instructions deterministically.
- M4A-E2-S2: Show only style-context warnings; never imply that a Soul is more or less competent for a Job.
- M4A-E2-S3: Prove that changing Soul does not change required workflow, tool grants, validators, quality thresholds, or approval rules.

Acceptance criteria:

- The instruction composition order is documented and tested.
- Restricted actions remain restricted under every Soul fixture.
- The same Job and Task Contract with different Souls produces distinguishable communication while meeting identical correctness, evidence, validation, and completion requirements.
- Soul fixtures cannot skip required steps, reduce review, alter output schemas, or trade correctness for a personality trait.
- Employee levels are absent from the UI; Soul style traits never appear as professional performance scores.

Suggested commits:

1. `feat(agent-runtime): compose governed Job and Soul identity`
2. `feat(souls): add Job compatibility checks`
3. `test(agent-runtime): prove Soul cannot expand authority`

### Epic M4A-E3 — Portable Soul bundle

Bundle shape:

```text
maya.soul/
├── manifest.json
├── soul.md
├── avatar.webp
└── examples/
    ├── introduction.md
    └── disagreement.md
```

#### Stories

- M4A-E3-S1: Export a Soul bundle.
- M4A-E3-S2: Preview and import a bundle.
- M4A-E3-S3: Reject malformed, oversized, or unsafe bundles.

Suggested commits:

1. `feat(souls): export portable Soul bundles`
2. `feat(souls): preview validate and import Soul bundles`
3. `test(souls): cover untrusted bundle handling`

### M4A release gate

- A Soul can be exported and imported into a clean local instance.
- Appointing different Souls visibly changes compliant communication style.
- Professional competence, required execution, tool grants, task acceptance, validation, quality thresholds, and approval requirements remain Job-controlled.
- Version history and author/source metadata are preserved.

## 11. Milestone M4B — Content Production Vertical

### Objective

Prove FounderOS as a durable production tool by turning one real topic into a reviewed, evidence-backed, founder-approved artifact.

### Epic M4B-E1 — Content domain and workspace

#### Stories

- M4B-E1-S1: Create a ContentItem from a founder brief.
- M4B-E1-S2: Track production stage, accountable Assignment, status, and deadline.
- M4B-E1-S3: Store brief, research, outline, draft, edit, fact-check, and final artifacts as versions.
- M4B-E1-S4: Display production and audit views.

Suggested commits:

1. `feat(content-domain): add content item and production stages`
2. `feat(content-domain): add versioned content artifacts and evidence links`
3. `feat(content): connect Content Studio to production APIs`
4. `test(content): cover content state and version rules`

### Epic M4B-E2 — Specialist execution

#### Stories

- M4B-E2-S1: Content Lead produces a structured angle and audience recommendation.
- M4B-E2-S2: Researcher records claims, sources, contradictory evidence, and confidence.
- M4B-E2-S3: Writer produces a draft from approved brief and evidence.
- M4B-E2-S4: Editor returns actionable edits or approval.
- M4B-E2-S5: Fact Checker verifies material claims independently.

Acceptance criteria:

- Each specialist receives a bounded Task Contract.
- Each stage has explicit inputs, output schema, validator, retry limit, and failure escalation.
- Execution cannot silently skip an incomplete required stage.
- Specialist output is attributable to an Assignment, Job version, Soul version, model, and tool observations.

Suggested commits:

1. `feat(content-runtime): add content lead and research contracts`
2. `feat(content-runtime): add writer and editor contracts`
3. `feat(content-runtime): add independent fact check contract`
4. `test(content-runtime): evaluate specialist outputs and failure paths`

### Epic M4B-E3 — Founder approval and recovery

#### Stories

- M4B-E3-S1: Escalate angle and publication decisions to Founder Inbox.
- M4B-E3-S2: Resume from the same workflow checkpoint after a decision.
- M4B-E3-S3: Return failed review to the appropriate upstream stage.
- M4B-E3-S4: Recover after backend or orchestrator restart without duplicate artifacts.

Suggested commits:

1. `feat(content-workflow): add founder approval interrupts`
2. `feat(content-workflow): add review rejection and bounded revision`
3. `test(content-workflow): prove restart recovery and artifact idempotency`

### Epic M4B-E4 — End-to-end founder experience

#### Stories

- M4B-E4-S1: Start a content item from Command Center.
- M4B-E4-S2: Observe truthful live progress and blockers.
- M4B-E4-S3: Compare evidence, reviews, and versions.
- M4B-E4-S4: Approve and retrieve the final publishable artifact.

Suggested commits:

1. `feat(command-center): launch and monitor content work`
2. `feat(content): add founder review and approval experience`
3. `test(e2e): prove topic to publishable artifact journey`
4. `docs(m4): document content production operation and evidence`

### M4B release gate

- One real topic completes the full workflow.
- All material claims retain source provenance or an explicit unverified state.
- A failed fact check triggers revision rather than publication.
- Founder approval is required for the final publishable state.
- Service restart does not lose state or duplicate outputs.
- The final artifact can be copied or exported for publication.

## 12. Milestone M5 — Package Ecosystem

### Objective

Allow Jobs, Souls, organizations, and team configurations to be safely shared without building an online marketplace first.

### Epic M5-E1 — Package specifications

#### Stories

- M5-E1-S1: Define Job Plugin manifest and bundle schema.
- M5-E1-S2: Stabilize Soul bundle schema.
- M5-E1-S3: Define Organization Template and Team Pack schemas.
- M5-E1-S4: Define semantic version, dependency, compatibility, and capability declarations.

Suggested commits:

1. `docs(packages): specify Job Soul organization and team bundles`
2. `feat(packages): add versioned manifest schemas`
3. `test(packages): validate package compatibility rules`

### Epic M5-E2 — Safe installation lifecycle

#### Stories

- M5-E2-S1: Preview package contents and permissions before installation.
- M5-E2-S2: Install with provenance and integrity metadata.
- M5-E2-S3: Preview upgrades and conflicts.
- M5-E2-S4: Analyze uninstall impact before removal.

Acceptance criteria:

- Imported packages are untrusted by default.
- A Soul package cannot request tools.
- Job capabilities require explicit founder approval.
- Package prompts cannot override platform or company governance.
- Install, upgrade, and uninstall operations are audited.

Suggested commits:

1. `feat(packages): add package inspection and permission preview`
2. `feat(packages): add audited local installation`
3. `feat(packages): add upgrade conflict and uninstall analysis`
4. `test(packages): cover malicious and malformed package fixtures`

### Epic M5-E3 — Local library experience

#### Stories

- M5-E3-S1: Browse installed and available local packages.
- M5-E3-S2: Install a Team Pack into a new or existing company.
- M5-E3-S3: Resolve Position and Soul conflicts during installation.
- M5-E3-S4: Export a shareable package from the current organization.

Suggested commits:

1. `feat(library): add local Job Soul and team package browser`
2. `feat(library): add Team Pack install and conflict resolution`
3. `feat(library): export organization assets as a package`
4. `test(e2e): prove package sharing between clean instances`

### M5 release gate

- A Team Pack can be exported from one clean instance and installed into another.
- Installation never silently grants sensitive capabilities.
- Package source, version, integrity, and local modifications remain visible.
- Online discovery, ratings, purchases, and remote code execution remain out of scope.

## 13. Milestone M6 — Live Feedback and Product Polish

### Objective

Make real work feel alive and human while preserving productivity, accessibility, and truthful system state.

### Epic M6-E1 — Live activity

#### Stories

- M6-E1-S1: Stream or poll task, workflow, and artifact activity.
- M6-E1-S2: Show working, waiting, blocked, reviewing, and failed states.
- M6-E1-S3: Navigate from an activity event to its task, artifact, evidence, or decision.

Suggested commits:

1. `feat(activity): add authoritative company activity feed`
2. `feat(frontend): add live task and assignment states`
3. `test(activity): prove truthful state transitions`

### Epic M6-E2 — Purposeful delight

#### Stories

- M6-E2-S1: Animate real task handoffs and appointments.
- M6-E2-S2: Celebrate meaningful production milestones.
- M6-E2-S3: Provide a company timeline and project replay.
- M6-E2-S4: Respect reduced motion and provide equivalent static feedback.

Suggested commits:

1. `feat(experience): add truthful handoff and appointment feedback`
2. `feat(experience): add milestone celebration and timeline replay`
3. `test(experience): cover reduced motion and accessibility`

### Epic M6-E3 — Operational transparency

#### Stories

- M6-E3-S1: Show cost, elapsed time, attempts, and current blocker in audit mode.
- M6-E3-S2: Explain why a task was assigned to an Agent Instance.
- M6-E3-S3: Explain why a decision was escalated to the founder.
- M6-E3-S4: Make retries, cancellations, and failures inspectable.

Suggested commits:

1. `feat(audit): add task execution and cost inspection`
2. `feat(audit): explain routing escalation and review decisions`
3. `test(audit): cover failure retry and cancellation visibility`

### M6 release gate

- Every animated or live state maps to authoritative backend state.
- Default screens remain focused on work and decisions, not traces.
- Audit details are available without overwhelming the daily founder experience.
- Milestone feedback rewards real output, not engagement for its own sake.

## 14. Cross-cutting epics

These run across milestones and must not be deferred to the end.

### Epic X1 — Accessibility

- Keyboard-complete organization editing.
- Text alternatives for status and relationship colors.
- Reduced-motion support.
- Screen-reader names for nodes, edges, avatars, progress, and decisions.
- Focus restoration after dialogs, mutations, and canvas operations.

### Epic X2 — Security and governance

- Product authentication before external deployment.
- Company-scoped authorization.
- Permission previews for organization and package changes.
- No secrets in frontend bundles or package files.
- Safe rendering of imported Markdown and media.
- Explicit approval for external publishing, spending, destructive operations, and new tool grants.

### Epic X3 — Reliability and observability

- Stable request IDs and error contracts.
- Idempotent mutation keys for consequential commands.
- Conflict handling for concurrent organization edits.
- Structured activity and audit events.
- Frontend error boundaries and recoverable retry states.

### Epic X4 — Testing

- Unit tests for domain transformations and validation.
- Component tests for stateful visual components.
- Contract tests between frontend DTOs and backend DTOs.
- Browser tests for founder-critical journeys.
- Backend integration tests for persistence, constraints, and compatibility.
- Restart smoke tests for content workflows.

## 15. Commit discipline

Each commit must represent one reviewable behavior or enabling change. A milestone should not land as one bulk commit.

Preferred order inside an epic:

```text
1. docs or schema decision when the contract is material
2. domain types and constraints
3. behavior or UI
4. integration boundary
5. tests
6. operational documentation and evidence
```

Preferred prefixes:

```text
feat(frontend):
feat(command-center):
feat(organization):
feat(souls):
feat(content):
feat(org-domain):
feat(org-api):
feat(packages):
refactor(tasks):
refactor(slack):
test(...):
docs(...):
fix(...):
```

Commit rules:

- Do not combine schema migration, broad domain refactor, and visual redesign in one commit.
- A Flyway migration and the minimum entity/repository support may share a commit when they are inseparable.
- Keep generated files and dependency lock changes with the feature that requires them.
- Add regression coverage in the same epic that changes behavior.
- Do not remove compatibility fields until a later commit proves all reads and writes have moved.
- Record verification evidence at each milestone release gate.

## 16. Refactor decisions

### Preserve

- Spring Boot as business system of record.
- FastAPI/LangGraph as workflow runtime.
- PostgreSQL business records and workflow checkpoints.
- Project, FounderDecision, WorkflowRun, Artifact, and WorkTask concepts.
- Slack durable inbox/outbox boundary.
- Human approval, idempotency, and restart recovery behavior.

### Replace gradually

- Fixed `AgentRole` as the identity and routing key.
- `AgentProfile` as a combined role/persona concept.
- `WorkTask.accountableAgentRole` as the only accountable identity.
- Slack bindings that can address only a fixed enum role.
- Artifact models that cannot express producer, provenance, review state, and relationships between versions.

### Do not build yet

- Online marketplace or payments.
- 3D virtual office.
- artificial currencies, daily rewards, or engagement loops.
- unconstrained multi-agent debate.
- complex matrix organizations and committees.
- automatic promotions or relationship drama.
- GitHub engineering team automation.
- WhatsApp escalation.
- arbitrary remote plugin code execution.

## 17. First implementation slice

The first implementation task after this plan is approved is M3A-E1-S1, followed by the application shell and typed mock contracts.

The first demonstration should use one company with:

```text
Founder
└── Content Lead — Maya
    ├── Researcher — Iris
    ├── Writer — Nova
    ├── Editor — Ada
    └── Fact Checker — Guardian
```

The demonstration storyline must include:

1. one active content item;
2. one piece of contradictory evidence;
3. one editor disagreement;
4. one founder decision;
5. one failed fact-check state;
6. one successful revision and final artifact;
7. one vacant Position visible in Organization Studio.

This provides enough product tension to test clarity, personality, governance, and real production value without requiring autonomous execution in the first frontend milestone.
