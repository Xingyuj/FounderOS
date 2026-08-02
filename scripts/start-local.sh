#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"
docker compose up -d postgres
until docker compose exec -T postgres pg_isready -U founder_os -d founder_os >/dev/null 2>&1; do sleep 1; done
if [[ ! -x orchestrator/.venv/bin/uvicorn ]]; then
  echo "Missing orchestrator environment. Run: cd orchestrator && python3.12 -m venv .venv && .venv/bin/pip install -e '.[test]'" >&2
  exit 1
fi
(cd orchestrator && exec .venv/bin/uvicorn app.main:app --host 0.0.0.0 --port "${ORCHESTRATOR_PORT:-8000}") &
ORCHESTRATOR_PID=$!
(cd backend && exec ./mvnw spring-boot:run) &
BACKEND_PID=$!
trap 'kill "$ORCHESTRATOR_PID" "$BACKEND_PID" 2>/dev/null || true' EXIT INT TERM
wait "$ORCHESTRATOR_PID" "$BACKEND_PID"

