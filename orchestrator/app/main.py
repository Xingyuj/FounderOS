from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException, Request
from .graph import WorkflowEngine
from .repository import CheckpointRepository
from .schemas import *
from .settings import Settings

@asynccontextmanager
async def lifespan(app: FastAPI):
    settings = Settings()
    repository = CheckpointRepository(settings.database_uri)
    app.state.repository = repository
    app.state.engine = WorkflowEngine(repository.open())
    yield
    repository.close()

app = FastAPI(title="FounderOS Orchestrator", version="0.1.0", lifespan=lifespan)

@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "UP"}

@app.post("/internal/workflows", response_model=StartWorkflowResponse, response_model_by_alias=True)
def start_workflow(body: StartWorkflowRequest, request: Request) -> StartWorkflowResponse:
    try:
        decision = request.app.state.engine.start({"project_id": body.project_id, "thread_id": body.thread_id, "project_name": body.project_name, "idea": body.idea})
        return StartWorkflowResponse(threadId=body.thread_id, status="WAITING_FOR_FOUNDER", currentNode="wait_for_founder", decision=Decision(**decision))
    except ValueError as exc:
        raise HTTPException(status_code=409, detail=str(exc)) from exc

@app.post("/internal/workflows/{thread_id}/resume", response_model=ResumeWorkflowResponse, response_model_by_alias=True)
def resume_workflow(thread_id: str, body: ResumeWorkflowRequest, request: Request) -> ResumeWorkflowResponse:
    try:
        state = request.app.state.engine.resume(thread_id, {"selectedOption": body.selected_option, "comment": body.comment})
    except KeyError as exc:
        raise HTTPException(status_code=404, detail=str(exc.args[0])) from exc
    except ValueError as exc:
        raise HTTPException(status_code=409, detail=str(exc)) from exc
    return ResumeWorkflowResponse(threadId=thread_id, status="COMPLETED", currentNode="end", artifact=Artifact(title=f"{state['project_name']} Product Brief", content=state["product_brief"]))
