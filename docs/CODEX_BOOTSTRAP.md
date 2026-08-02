# Codex Bootstrap Task

You are operating in the FounderOS repository.

## Required preparation

Read these files completely in order:

1. `AGENTS.md`
2. `FOUNDER_OS_M1.md`
3. `.ai/PROJECT_CONTEXT.md`
4. `.ai/CURRENT_TASK.md`
5. `.ai/DECISIONS.md`
6. `.ai/HANDOFF.md`

Then inspect:

```bash
git status --short --branch
git log --oneline -10
find . -maxdepth 3 -type f | sort
```

Do not overwrite unrelated existing work.

## Assignment

Implement FounderOS Milestone 1 exactly as specified in `FOUNDER_OS_M1.md` and governed by `AGENTS.md`.

Work autonomously through the milestone. Do not stop after scaffolding. Continue through implementation, migrations, tests, local infrastructure, integration, smoke testing, and documentation until either:

1. the Definition of Done is satisfied; or
2. a genuine external blocker requires founder input.

Use deterministic workflow output initially; do not add an LLM provider.

## Required progress records

Update `.ai/CURRENT_TASK.md` after planning and after each major phase. Record durable choices in `.ai/DECISIONS.md`. Before ending, update `.ai/HANDOFF.md` with:

- work completed;
- files changed;
- commands and tests run;
- exact results;
- unresolved issues;
- exact next action.

## Completion response

Report only evidence-backed status. Include the smoke-test result and identify anything not completed.
