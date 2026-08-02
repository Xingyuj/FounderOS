from typing import TypedDict
from pydantic import BaseModel, Field

class Decision(BaseModel):
    question: str
    options: list[str]
    recommendation: str
    context: str

class Artifact(BaseModel):
    type: str = "PRODUCT_BRIEF"
    title: str
    content: str

class StartWorkflowRequest(BaseModel):
    project_id: str = Field(alias="projectId")
    thread_id: str = Field(alias="threadId")
    project_name: str = Field(alias="projectName", min_length=1)
    idea: str = Field(min_length=1)

class ResumeWorkflowRequest(BaseModel):
    selected_option: str = Field(alias="selectedOption", min_length=1)
    comment: str | None = None

class StartWorkflowResponse(BaseModel):
    thread_id: str = Field(alias="threadId")
    status: str
    current_node: str = Field(alias="currentNode")
    decision: Decision
    model_config = {"populate_by_name": True}

class ResumeWorkflowResponse(BaseModel):
    thread_id: str = Field(alias="threadId")
    status: str
    current_node: str = Field(alias="currentNode")
    artifact: Artifact
    model_config = {"populate_by_name": True}

class FounderState(TypedDict, total=False):
    project_id: str
    thread_id: str
    project_name: str
    idea: str
    analysis: str | None
    decision: dict | None
    founder_response: dict | None
    product_brief: str | None

