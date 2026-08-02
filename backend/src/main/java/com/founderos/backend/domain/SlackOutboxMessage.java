package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "slack_outbox_message")
public class SlackOutboxMessage {
    @Id private UUID id;
    @Column(name = "slack_team_id", nullable = false, length = 40) private String slackTeamId;
    @Column(name = "slack_channel_id", nullable = false, length = 40) private String slackChannelId;
    @Column(name = "slack_thread_ts", length = 40) private String slackThreadTs;
    @Enumerated(EnumType.STRING) @Column(name = "agent_role", nullable = false, length = 40) private AgentRole agentRole;
    @Column(nullable = false, columnDefinition = "text") private String text;
    @Column(name = "blocks_json", columnDefinition = "text") private String blocksJson;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private QueueStatus status;
    @Column(name = "attempt_count", nullable = false) private int attemptCount;
    @Column(name = "available_at", nullable = false) private Instant availableAt;
    @Column(name = "slack_message_ts", length = 40) private String slackMessageTs;
    @Column(name = "error_message", columnDefinition = "text") private String errorMessage;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Column(name = "delivered_at") private Instant deliveredAt;
    protected SlackOutboxMessage() {}
    public SlackOutboxMessage(String teamId, String channelId, String threadTs, AgentRole role, String text, String blocksJson) {
        this.id = UUID.randomUUID(); this.slackTeamId = teamId; this.slackChannelId = channelId; this.slackThreadTs = threadTs; this.agentRole = role; this.text = text; this.blocksJson = blocksJson; this.status = QueueStatus.PENDING; this.availableAt = Instant.now(); this.createdAt = this.updatedAt = Instant.now();
    }
    public void processing() { status = QueueStatus.PROCESSING; attemptCount++; updatedAt = Instant.now(); }
    public void delivered(String messageTs) { status = QueueStatus.DELIVERED; slackMessageTs = messageTs; deliveredAt = Instant.now(); updatedAt = deliveredAt; errorMessage = null; }
    public void retry(String message) { status = QueueStatus.PENDING; errorMessage = safe(message); availableAt = Instant.now().plusSeconds(Math.min(300, 1L << Math.min(attemptCount, 8))); updatedAt = Instant.now(); }
    public void failed(String message) { status = QueueStatus.FAILED; errorMessage = safe(message); updatedAt = Instant.now(); }
    private String safe(String message) { return message == null ? "Unknown delivery failure" : message.substring(0, Math.min(message.length(), 1000)); }
    public UUID getId() { return id; } public String getSlackTeamId() { return slackTeamId; } public String getSlackChannelId() { return slackChannelId; } public String getSlackThreadTs() { return slackThreadTs; } public AgentRole getAgentRole() { return agentRole; } public String getText() { return text; } public String getBlocksJson() { return blocksJson; } public QueueStatus getStatus() { return status; } public int getAttemptCount() { return attemptCount; } public Instant getAvailableAt() { return availableAt; } public String getSlackMessageTs() { return slackMessageTs; }
}
