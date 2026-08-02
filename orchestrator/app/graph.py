from typing import Any
from langgraph.graph import StateGraph, START, END
from langgraph.types import Command, interrupt
from .schemas import FounderState

DECISION = {
    "question": "Who should the first version serve?",
    "options": ["Beginner investors", "Experienced non-programmers", "Professional quant traders"],
    "recommendation": "Experienced non-programmers",
    "context": "The target segment affects product complexity, onboarding, and the MVP feature set.",
}

def analyse_idea(state: FounderState) -> dict[str, str]:
    return {"analysis": f"{state['project_name']} addresses this idea: {state['idea']} The first blocking decision is the initial target customer segment."}

def create_founder_question(_: FounderState) -> dict[str, dict[str, Any]]:
    return {"decision": DECISION.copy()}

def wait_for_founder(state: FounderState) -> dict[str, dict[str, str | None]]:
    response = interrupt(state["decision"])
    return {"founder_response": response}

def generate_product_brief(state: FounderState) -> dict[str, str]:
    response = state["founder_response"] or {}
    comment = response.get("comment") or "No additional direction provided."
    content = f"""# {state['project_name']} Product Brief

## Original Idea
{state['idea']}

## Target User
{response['selectedOption']}

## Founder Direction
{comment}

## Problem Statement
The target user needs a clear, accessible way to turn an idea into a validated project without unnecessary complexity.

## Proposed MVP
- Strategy builder
- Basic validation
- Backtest summary
- Saved projects

## Non-Goals
- Automated trading
- Brokerage execution
- Professional quantitative research infrastructure

## Next Recommended Step
Produce an implementation-ready Product Requirements Document.
"""
    return {"product_brief": content}

def build_graph(checkpointer: Any):
    graph = StateGraph(FounderState)
    graph.add_node("analyse_idea", analyse_idea)
    graph.add_node("create_founder_question", create_founder_question)
    graph.add_node("wait_for_founder", wait_for_founder)
    graph.add_node("generate_product_brief", generate_product_brief)
    graph.add_edge(START, "analyse_idea")
    graph.add_edge("analyse_idea", "create_founder_question")
    graph.add_edge("create_founder_question", "wait_for_founder")
    graph.add_edge("wait_for_founder", "generate_product_brief")
    graph.add_edge("generate_product_brief", END)
    return graph.compile(checkpointer=checkpointer)

class WorkflowEngine:
    def __init__(self, checkpointer: Any):
        self.graph = build_graph(checkpointer)

    @staticmethod
    def _config(thread_id: str) -> dict[str, dict[str, str]]:
        return {"configurable": {"thread_id": thread_id}}

    def start(self, state: FounderState) -> dict[str, Any]:
        config = self._config(state["thread_id"])
        existing = self.graph.get_state(config)
        if existing.values:
            raise ValueError("Workflow thread already exists")
        result = self.graph.invoke(state, config=config)
        interrupts = result.get("__interrupt__", ())
        if not interrupts:
            raise RuntimeError("Workflow did not pause for founder input")
        return interrupts[0].value

    def resume(self, thread_id: str, response: dict[str, str | None]) -> FounderState:
        config = self._config(thread_id)
        snapshot = self.graph.get_state(config)
        if not snapshot.values:
            raise KeyError("Workflow thread not found")
        if not snapshot.next:
            raise ValueError("Workflow is not waiting for founder input")
        return self.graph.invoke(Command(resume=response), config=config)

