# Current Task

## Objective

Deliver Milestone 3A Frontend Experience Discovery: a realistic, typed-mock FounderOS product experience spanning Command Center, Content Studio, and Organization Studio.

## Current status

M1 remains complete. The local M2 Slack Team Experience remains complete. The M3A application, automated tests, production build, and Chromium journey are complete. The required moderated founder walkthrough remains a human validation gate; real Slack workspace installation also remains externally blocked.

## Implementation plan

1. Bootstrap the React/TypeScript application and quality commands.
2. Define product vocabulary, shared DTOs, API client, and stateful HTTP mocks.
3. Implement the Command Center, founder decision flow, and truthful activity view.
4. Implement Content Studio evidence, review, versions, and audit views.
5. Implement Organization Studio canvas, vacancy, and vocabulary guidance.
6. Add component and browser journeys, run regression suites, and align documentation.

## Recommended execution sequence

1. Conduct the moderated product walkthrough.
2. Record findings without expanding secondary screens.
3. Accept or revise the four-term product vocabulary.
4. Freeze the first M3B organization API contract.
5. Only then begin the additive organization persistence migration.

## Completion target

The founder can understand company state quickly, resolve the blocking content decision, inspect evidence/reviews/artifact versions, distinguish Job/Soul/Position/Assignment, and see a vacant Position in a realistic mock company.

## Verification evidence

- Frontend lint and type-check: PASS.
- Frontend Vitest: 5 passed.
- Frontend production build: PASS.
- Playwright Chromium founder journey: 1 passed.
- Backend regression suite: 8 passed.
- Orchestrator regression suite: 3 passed with one existing upstream pending-deprecation warning.

## M3A implementation choices

- One API client serves mock and future real product APIs.
- MSW owns mock transport and mutable scenario state; feature components do not import server fixtures.
- Organization layout remains explicitly separate from authority and reporting semantics.
- Production and audit views separate daily work from execution detail.
- Mock data is visibly labelled and never presented as live agent execution.

## Exact next task

Run one moderated founder walkthrough of the M3A storyline, record usability findings, then review and freeze the smallest M3B organization contract (`GetOrganization`, `ListJobDefinitions`, `ListSoulDefinitions`, `CreatePosition`, `AppointSoulToPosition`, and `UpdateOrganizationLayout`). Do not start Flyway organization migrations until that review is complete.
