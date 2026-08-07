# FounderOS Frontend

M3A is a contract-driven product prototype for the Command Center, Content Studio, Organization Studio, and Talent Guild. It runs against Mock Service Worker by default and does not require the backend or orchestrator.

## Local development

```bash
npm install
npm run dev
```

Open `http://localhost:5173`. The default `content-review` scenario includes a blocked content item, contradictory evidence, an editor disagreement, a failed fact check, an open founder decision, a successful revision candidate, and a vacant Position.

The Talent Library at `/talent` creates employee Souls independently of Positions. New employees enter the available pool with no Job authority. A separate action on a vacant Position in Organization Studio appoints one available employee and creates the Assignment. `/hire` remains a compatibility redirect.

## Quality checks

```bash
npm run lint
npm run typecheck
npm test
npm run build
npx playwright install chromium
npm run test:e2e
```

Configuration is documented in `.env.example`. Set `VITE_ENABLE_MOCKS=false` only when a compatible product API is available. Feature components always use the shared typed API client; they do not import fixture data as server state.

## Product vocabulary

- A **Job** defines reusable responsibility and authority.
- A **Soul** defines portable identity, values, and voice.
- A **Position** is a seat in one company.
- An **Assignment** appoints one Soul to one Position.

M3A deliberately does not persist organization changes. That begins after the first organization contract is reviewed in M3B.
