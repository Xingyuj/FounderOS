CREATE TABLE agent_profile (
  role VARCHAR(40) PRIMARY KEY,
  display_name VARCHAR(100) NOT NULL UNIQUE,
  responsibility TEXT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL
);

INSERT INTO agent_profile(role, display_name, responsibility, created_at) VALUES
  ('CHIEF_OF_STAFF', 'Chief of Staff', 'Triage, coordination, summaries, and founder escalation', now()),
  ('PRODUCT_LEAD', 'Product Lead', 'Product definition, requirements, scope, and product artifacts', now()),
  ('RESEARCH_ANALYST', 'Research Analyst', 'Bounded market, customer, competitor, and factual research', now()),
  ('ENGINEERING_LEAD', 'Engineering Lead', 'Architecture, delivery planning, technical risk, and verification', now()),
  ('GROWTH_LEAD', 'Growth Lead', 'Positioning, launch planning, growth experiments, and measurement', now());

CREATE TABLE slack_channel_binding (
  id UUID PRIMARY KEY,
  slack_team_id VARCHAR(40) NOT NULL,
  slack_channel_id VARCHAR(40) NOT NULL,
  channel_kind VARCHAR(40) NOT NULL,
  primary_agent_role VARCHAR(40) NOT NULL REFERENCES agent_profile(role),
  project_id UUID REFERENCES project(id),
  label VARCHAR(100),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT uq_slack_channel_binding UNIQUE(slack_team_id, slack_channel_id),
  CONSTRAINT ck_slack_channel_kind CHECK (channel_kind IN ('FUNCTIONAL_CHANNEL','PROJECT_CHANNEL')),
  CONSTRAINT ck_project_channel_project CHECK (
    (channel_kind = 'PROJECT_CHANNEL' AND project_id IS NOT NULL) OR
    (channel_kind = 'FUNCTIONAL_CHANNEL' AND project_id IS NULL)
  )
);
CREATE UNIQUE INDEX uq_active_project_slack_channel ON slack_channel_binding(project_id) WHERE project_id IS NOT NULL;

CREATE TABLE slack_conversation (
  id UUID PRIMARY KEY,
  slack_team_id VARCHAR(40) NOT NULL,
  slack_channel_id VARCHAR(40) NOT NULL,
  slack_thread_ts VARCHAR(40) NOT NULL,
  kind VARCHAR(40) NOT NULL,
  project_id UUID REFERENCES project(id),
  agent_role VARCHAR(40) REFERENCES agent_profile(role),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT uq_slack_conversation UNIQUE(slack_team_id, slack_channel_id, slack_thread_ts),
  CONSTRAINT ck_slack_conversation_kind CHECK (kind IN ('DM','FUNCTIONAL_CHANNEL','PROJECT_CHANNEL'))
);

CREATE TABLE work_task (
  id UUID PRIMARY KEY,
  project_id UUID REFERENCES project(id),
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  status VARCHAR(40) NOT NULL,
  accountable_agent_role VARCHAR(40) NOT NULL REFERENCES agent_profile(role),
  created_by VARCHAR(20) NOT NULL,
  source_conversation_id UUID REFERENCES slack_conversation(id),
  parent_task_id UUID REFERENCES work_task(id),
  result TEXT,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  completed_at TIMESTAMPTZ,
  CONSTRAINT ck_work_task_status CHECK (status IN ('NEW','ASSIGNED','IN_PROGRESS','WAITING_FOR_FOUNDER','BLOCKED','COMPLETED','CANCELLED','FAILED')),
  CONSTRAINT ck_work_task_creator CHECK (created_by IN ('FOUNDER','AGENT','SYSTEM')),
  CONSTRAINT ck_work_task_not_self_parent CHECK (parent_task_id IS NULL OR parent_task_id <> id)
);
CREATE INDEX idx_work_task_project ON work_task(project_id);
CREATE INDEX idx_work_task_status ON work_task(status);
CREATE INDEX idx_work_task_agent ON work_task(accountable_agent_role);

ALTER TABLE slack_conversation ADD COLUMN task_id UUID REFERENCES work_task(id);

CREATE TABLE slack_inbox_event (
  id UUID PRIMARY KEY,
  external_id VARCHAR(200) NOT NULL UNIQUE,
  event_type VARCHAR(60) NOT NULL,
  slack_team_id VARCHAR(40) NOT NULL,
  slack_user_id VARCHAR(40) NOT NULL,
  slack_channel_id VARCHAR(40) NOT NULL,
  slack_message_ts VARCHAR(40),
  slack_thread_ts VARCHAR(40),
  channel_type VARCHAR(20),
  text TEXT,
  action_token UUID,
  founder_comment TEXT,
  status VARCHAR(30) NOT NULL,
  attempt_count INTEGER NOT NULL DEFAULT 0,
  available_at TIMESTAMPTZ NOT NULL,
  processed_at TIMESTAMPTZ,
  error_message TEXT,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT ck_slack_inbox_status CHECK (status IN ('PENDING','PROCESSING','PROCESSED','FAILED','REJECTED')),
  CONSTRAINT ck_slack_inbox_attempts CHECK (attempt_count >= 0)
);
CREATE INDEX idx_slack_inbox_pending ON slack_inbox_event(status, available_at);

CREATE TABLE slack_outbox_message (
  id UUID PRIMARY KEY,
  slack_team_id VARCHAR(40) NOT NULL,
  slack_channel_id VARCHAR(40) NOT NULL,
  slack_thread_ts VARCHAR(40),
  agent_role VARCHAR(40) NOT NULL REFERENCES agent_profile(role),
  text TEXT NOT NULL,
  blocks_json TEXT,
  status VARCHAR(30) NOT NULL,
  attempt_count INTEGER NOT NULL DEFAULT 0,
  available_at TIMESTAMPTZ NOT NULL,
  slack_message_ts VARCHAR(40),
  error_message TEXT,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  delivered_at TIMESTAMPTZ,
  CONSTRAINT ck_slack_outbox_status CHECK (status IN ('PENDING','PROCESSING','DELIVERED','FAILED')),
  CONSTRAINT ck_slack_outbox_attempts CHECK (attempt_count >= 0)
);
CREATE INDEX idx_slack_outbox_pending ON slack_outbox_message(status, available_at);

CREATE TABLE slack_decision_action (
  token UUID PRIMARY KEY,
  decision_id UUID NOT NULL REFERENCES founder_decision(id),
  selected_option TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  consumed_at TIMESTAMPTZ,
  CONSTRAINT uq_slack_decision_option UNIQUE(decision_id, selected_option)
);
