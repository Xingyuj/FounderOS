# HTTP API

- `POST /api/projects` with `{ "name": string, "idea": string }` creates a project and returns its open decision (201).
- `GET /api/projects/{id}` returns the project, all decisions, workflow run, and artifacts.
- `POST /api/decisions/{id}/resolve` with `{ "selectedOption": string, "comment"?: string }` resumes the workflow and returns the Product Brief.
- `GET /api/artifacts/{id}` returns an artifact.
- `POST /internal/workflows` and `POST /internal/workflows/{threadId}/resume` are orchestrator-only coordination APIs.

## Slack Team API

- `POST /integrations/slack/events` receives signed Slack Events API envelopes and persists accepted founder messages before returning.
- `POST /integrations/slack/interactions` receives signed interactive payloads and queues opaque decision actions.
- `GET /api/slack/agents` lists the fixed M2 roster.
- `PUT /api/slack/channels/{channelId}` binds a Slack channel ID to a functional owner or project.
- `GET /api/tasks` and `GET /api/tasks/{id}` expose durable Slack-originated work for local administration.
- `POST /api/slack/decisions/{id}/publish` queues an open Founder Decision for Slack delivery.

The `/api/slack/**` and `/api/tasks/**` administration endpoints require `X-FounderOS-Admin-Token`. Slack ingress uses `X-Slack-Request-Timestamp` and `X-Slack-Signature` verification plus configured workspace/founder allow-lists.

Invalid input/options return 400, missing records/threads return 404, duplicate resolution/thread conflicts return 409, orchestrator unavailability returns 503, and invalid orchestrator responses return 502. Backend errors use RFC 7807 Problem Details.

## M3A frontend contract candidates

M3A exercises the following product-facing routes through Mock Service Worker. They are typed contract candidates, not implemented Spring Boot endpoints yet:

- `GET /api/dashboard` returns Company, active Tasks, Founder Decisions, Activity, and the latest Artifact.
- `GET /api/content-items` returns staged ContentItems with evidence, reviews, and versioned Artifacts.
- `GET /api/organizations/current` returns Company, Job Definitions, Soul Definitions, Positions, and active Assignments.
- `POST /api/talent` creates an unassigned Soul in the available talent pool.
- `POST /api/positions/{id}/appoint` appoints one available Soul to one vacant Position and creates an Assignment.
- `POST /api/decisions/{id}/resolve` accepts `{ "selectedOptionId": string, "founderComment": string }` and mutates later mock reads.

These contracts contain no Slack transport fields and all frontend features use the same API client in mock and future real modes. M3B must review and freeze the smaller organization command/query contract before adding persistence.
