#!/usr/bin/env bash
set -euo pipefail
BACKEND_URL="${BACKEND_BASE_URL:-http://localhost:8080}"
ORCHESTRATOR_URL="${ORCHESTRATOR_BASE_URL:-http://localhost:8000}"
curl --fail --silent "$BACKEND_URL/actuator/health" | jq -e '.status == "UP"' >/dev/null
curl --fail --silent "$ORCHESTRATOR_URL/health" | jq -e '.status == "UP"' >/dev/null
CREATE_RESPONSE="$(curl --fail --silent -X POST "$BACKEND_URL/api/projects" -H 'Content-Type: application/json' -d '{"name":"Tradigo","idea":"A platform that helps retail investors design and evaluate trading strategies without writing code."}')"
PROJECT_ID="$(jq -er '.project.id' <<<"$CREATE_RESPONSE")"
DECISION_ID="$(jq -er '.decision.id' <<<"$CREATE_RESPONSE")"
THREAD_ID="$(jq -er '.project.workflowThreadId' <<<"$CREATE_RESPONSE")"
jq -e '.project.status == "WAITING_FOR_FOUNDER" and .decision.status == "OPEN"' <<<"$CREATE_RESPONSE" >/dev/null
INVALID_STATUS="$(curl --silent -o /dev/null -w '%{http_code}' -X POST "$BACKEND_URL/api/decisions/$DECISION_ID/resolve" -H 'Content-Type: application/json' -d '{"selectedOption":"Invalid audience"}')"
[[ "$INVALID_STATUS" == "400" ]]
RESOLVE_RESPONSE="$(curl --fail --silent -X POST "$BACKEND_URL/api/decisions/$DECISION_ID/resolve" -H 'Content-Type: application/json' -d '{"selectedOption":"Experienced non-programmers","comment":"Focus on Australian users initially."}')"
jq -e '.status == "COMPLETED" and .artifact.type == "PRODUCT_BRIEF" and .artifact.version == 1' <<<"$RESOLVE_RESPONSE" >/dev/null
DETAIL="$(curl --fail --silent "$BACKEND_URL/api/projects/$PROJECT_ID")"
jq -e --arg thread "$THREAD_ID" '.project.status == "COMPLETED" and .workflow.status == "COMPLETED" and .workflow.threadId == $thread and ([.artifacts[] | select(.type == "PRODUCT_BRIEF")] | length) == 1' <<<"$DETAIL" >/dev/null
DUPLICATE_STATUS="$(curl --silent -o /dev/null -w '%{http_code}' -X POST "$BACKEND_URL/api/decisions/$DECISION_ID/resolve" -H 'Content-Type: application/json' -d '{"selectedOption":"Experienced non-programmers"}')"
[[ "$DUPLICATE_STATUS" == "409" ]]
echo "PASS: project $PROJECT_ID paused on thread $THREAD_ID, resumed, and completed with exactly one Product Brief."

