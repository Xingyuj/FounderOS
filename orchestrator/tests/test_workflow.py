import pytest
from langgraph.checkpoint.memory import MemorySaver
from app.graph import DECISION, WorkflowEngine

def engine() -> WorkflowEngine:
    return WorkflowEngine(MemorySaver())

def test_workflow_interrupts_and_resumes_same_thread():
    workflow = engine()
    decision = workflow.start({"project_id":"p1","thread_id":"thread-1","project_name":"Tradigo","idea":"No-code trading strategies"})
    assert decision == DECISION
    state = workflow.resume("thread-1", {"selectedOption":"Experienced non-programmers","comment":"Focus on Australian users initially."})
    assert "Experienced non-programmers" in state["product_brief"]
    assert "Focus on Australian users initially." in state["product_brief"]
    assert state["thread_id"] == "thread-1"

def test_unknown_thread_is_clear_error():
    with pytest.raises(KeyError, match="Workflow thread not found"):
        engine().resume("missing", {"selectedOption":"x","comment":None})

def test_completed_thread_cannot_resume_again():
    workflow=engine(); workflow.start({"project_id":"p1","thread_id":"thread-1","project_name":"Tradigo","idea":"Idea"}); workflow.resume("thread-1", {"selectedOption":"Beginner investors","comment":None})
    with pytest.raises(ValueError, match="not waiting"):
        workflow.resume("thread-1", {"selectedOption":"Beginner investors","comment":None})

