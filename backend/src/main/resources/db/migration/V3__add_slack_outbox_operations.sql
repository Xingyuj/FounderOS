ALTER TABLE slack_outbox_message
  ADD COLUMN operation VARCHAR(20) NOT NULL DEFAULT 'POST',
  ADD COLUMN target_message_ts VARCHAR(40);

ALTER TABLE slack_outbox_message
  ADD CONSTRAINT ck_slack_outbox_operation CHECK (operation IN ('POST','UPDATE')),
  ADD CONSTRAINT ck_slack_outbox_update_target CHECK (
    (operation = 'POST' AND target_message_ts IS NULL) OR
    (operation = 'UPDATE' AND target_message_ts IS NOT NULL)
  );
