CREATE TABLE project (
  id UUID PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  idea TEXT NOT NULL,
  status VARCHAR(40) NOT NULL,
  workflow_thread_id VARCHAR(200),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT ck_project_status CHECK (status IN ('DISCOVERY','WAITING_FOR_FOUNDER','PRODUCT_DEFINITION','COMPLETED','FAILED'))
);
CREATE INDEX idx_project_status ON project(status);

CREATE TABLE founder_decision (
  id UUID PRIMARY KEY,
  project_id UUID NOT NULL REFERENCES project(id),
  workflow_thread_id VARCHAR(200) NOT NULL,
  question TEXT NOT NULL,
  options JSONB NOT NULL,
  recommendation TEXT,
  context TEXT,
  status VARCHAR(40) NOT NULL,
  selected_option TEXT,
  founder_comment TEXT,
  created_at TIMESTAMPTZ NOT NULL,
  resolved_at TIMESTAMPTZ,
  CONSTRAINT ck_decision_status CHECK (status IN ('OPEN','RESOLVED','CANCELLED'))
);
CREATE INDEX idx_decision_project ON founder_decision(project_id);
CREATE INDEX idx_decision_status ON founder_decision(status);

CREATE TABLE artifact (
  id UUID PRIMARY KEY,
  project_id UUID NOT NULL REFERENCES project(id),
  type VARCHAR(100) NOT NULL,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  version INTEGER NOT NULL CHECK (version > 0),
  created_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT uq_artifact_project_type_version UNIQUE(project_id, type, version)
);
CREATE INDEX idx_artifact_project ON artifact(project_id);

CREATE TABLE workflow_run (
  id UUID PRIMARY KEY,
  project_id UUID NOT NULL REFERENCES project(id),
  thread_id VARCHAR(200) NOT NULL UNIQUE,
  status VARCHAR(40) NOT NULL,
  current_node VARCHAR(150) NOT NULL,
  error_message TEXT,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT ck_workflow_status CHECK (status IN ('RUNNING','WAITING_FOR_FOUNDER','COMPLETED','FAILED'))
);
CREATE INDEX idx_workflow_project ON workflow_run(project_id);

