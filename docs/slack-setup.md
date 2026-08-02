# Slack Team Setup

FounderOS M2 uses one Slack App with five persisted agent personas. The app posts as FounderOS, while every message names the accountable persona, such as `Product Lead · FounderOS`. In a direct message, prefix a request with `Product Lead:`, `Research Analyst:`, `Engineering Lead:`, `Growth Lead:`, or `Chief of Staff:`. Chinese aliases `产品：`, `研究：`, `工程：`, `增长：`, and `秘书：` are also accepted. An unaddressed DM routes to Chief of Staff.

## Create the Slack App

1. Create an app from [`slack-app-manifest.yml`](../slack-app-manifest.yml).
2. Replace both `replace-with-your-host.example` request URLs with the public HTTPS address of the Spring Boot backend.
3. Install the app into the one allowed workspace.
4. Invite FounderOS into each functional or project channel it should observe. `chat:write.public` is deliberately not requested.
5. Copy the Signing Secret, Bot User OAuth Token, workspace ID, and founder Slack user ID into local environment variables.

The requested bot scopes are:

| Scope | Reason |
| --- | --- |
| `app_mentions:read` | Receive explicit FounderOS mentions. |
| `channels:history` | Receive messages in public channels to which the app has been invited. |
| `groups:history` | Receive messages in invited private channels. |
| `im:history` | Receive founder direct messages. |
| `chat:write` | Post task acknowledgements and update decision messages. |

No user token is required. FounderOS does not request `chat:write.public`; channel membership is an explicit allow-list enforced by Slack invitations and FounderOS channel bindings.

## Configure the Backend

Set these without committing their values:

```bash
SLACK_ENABLED=true
SLACK_SIGNING_SECRET=...
SLACK_BOT_TOKEN=xoxb-...
SLACK_TEAM_ID=T...
SLACK_FOUNDER_USER_ID=U...
SLACK_ADMIN_TOKEN=<a separate random local administration token>
```

Slack ingress rejects invalid signatures, requests more than five minutes old, the wrong workspace, and any user other than the configured founder. Bot messages are ignored to prevent loops. The admin token protects channel binding, agent inspection, task inspection, and decision publication APIs; send it in `X-FounderOS-Admin-Token`.

## Bind Channels

Bindings use immutable Slack channel IDs, not channel names.

```bash
curl -X PUT http://localhost:8080/api/slack/channels/C_PRODUCT \
  -H 'Content-Type: application/json' \
  -H "X-FounderOS-Admin-Token: $SLACK_ADMIN_TOKEN" \
  -d '{"kind":"FUNCTIONAL_CHANNEL","primaryAgentRole":"PRODUCT_LEAD","label":"product"}'
```

Recommended functional mappings:

| Slack channel | Agent role |
| --- | --- |
| `#founder-desk` | `CHIEF_OF_STAFF` |
| `#product` | `PRODUCT_LEAD` |
| `#engineering` | `ENGINEERING_LEAD` |
| `#growth` | `GROWTH_LEAD` |
| `#operations` | `CHIEF_OF_STAFF` |

Bind an existing project channel with a valid project UUID:

```bash
curl -X PUT http://localhost:8080/api/slack/channels/C_PROJECT \
  -H 'Content-Type: application/json' \
  -H "X-FounderOS-Admin-Token: $SLACK_ADMIN_TOKEN" \
  -d '{"kind":"PROJECT_CHANNEL","primaryAgentRole":"CHIEF_OF_STAFF","projectId":"PROJECT_UUID","label":"project-founder-os"}'
```

M2 does not create Slack channels automatically. A project has at most one primary project-channel binding.

## Decision Messages

Publish an existing open Founder Decision:

```bash
curl -X POST http://localhost:8080/api/slack/decisions/DECISION_UUID/publish \
  -H 'Content-Type: application/json' \
  -H "X-FounderOS-Admin-Token: $SLACK_ADMIN_TOKEN" \
  -d '{"channelId":"C_FOUNDER"}'
```

The backend creates opaque action tokens for the stored options. A click is acknowledged immediately, resolved through the existing authoritative decision workflow by the inbox worker, and reflected using `chat.update`. Repeated deliveries and repeated clicks do not resume the workflow twice.

## Reliability and Recovery

Verified Slack requests are stored before acknowledgement. PostgreSQL-backed inbox and outbox workers claim pending records, retry transient failures with bounded backoff, and restore interrupted `PROCESSING` records to `PENDING` after application restart. Permanent validation, authorization, token, and payload failures are retained as `FAILED` records with sanitized error text.

Run the automated Slack-compatible proof with:

```bash
cd backend && ./mvnw -Dtest=SlackTeamIntegrationTest test
```

This covers signed events, replay age, workspace/user authorization, bot-loop filtering, duplicate delivery, functional-channel routing, direct-message persona routing, decision buttons, duplicate clicks, queue delivery, and persisted `chat.update` intent. A real Slack workspace smoke test additionally requires valid Slack credentials and a public HTTPS request URL.

## Current M2 Boundary

M2 produces durable assignments and deterministic acknowledgements. The personas do not yet run independent LLM reasoning or external tools; that is M3. The original M1 HTTP APIs also remain intended for a trusted local environment and do not yet provide general user authentication.
