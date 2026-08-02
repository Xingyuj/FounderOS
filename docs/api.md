# HTTP API

- `POST /api/projects` with `{ "name": string, "idea": string }` creates a project and returns its open decision (201).
- `GET /api/projects/{id}` returns the project, all decisions, workflow run, and artifacts.
- `POST /api/decisions/{id}/resolve` with `{ "selectedOption": string, "comment"?: string }` resumes the workflow and returns the Product Brief.
- `GET /api/artifacts/{id}` returns an artifact.
- `POST /internal/workflows` and `POST /internal/workflows/{threadId}/resume` are orchestrator-only coordination APIs.

Invalid input/options return 400, missing records/threads return 404, duplicate resolution/thread conflicts return 409, orchestrator unavailability returns 503, and invalid orchestrator responses return 502. Backend errors use RFC 7807 Problem Details.

