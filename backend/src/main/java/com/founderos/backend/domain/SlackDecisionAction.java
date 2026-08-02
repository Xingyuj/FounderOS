package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "slack_decision_action")
public class SlackDecisionAction {
    @Id private UUID token;
    @Column(name = "decision_id", nullable = false) private UUID decisionId;
    @Column(name = "selected_option", nullable = false, columnDefinition = "text") private String selectedOption;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "consumed_at") private Instant consumedAt;
    protected SlackDecisionAction() {}
    public SlackDecisionAction(UUID decisionId, String option) { this.token = UUID.randomUUID(); this.decisionId = decisionId; this.selectedOption = option; this.createdAt = Instant.now(); }
    public void consume() { if (consumedAt == null) consumedAt = Instant.now(); }
    public UUID getToken() { return token; } public UUID getDecisionId() { return decisionId; } public String getSelectedOption() { return selectedOption; } public Instant getConsumedAt() { return consumedAt; }
}
