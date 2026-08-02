package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "slack_inbox_event")
public class SlackInboxEvent {
    @Id private UUID id;
    @Column(name = "external_id", nullable = false, unique = true, length = 200) private String externalId;
    @Column(name = "event_type", nullable = false, length = 60) private String eventType;
    @Column(name = "slack_team_id", nullable = false, length = 40) private String slackTeamId;
    @Column(name = "slack_user_id", nullable = false, length = 40) private String slackUserId;
    @Column(name = "slack_channel_id", nullable = false, length = 40) private String slackChannelId;
    @Column(name = "slack_message_ts", length = 40) private String slackMessageTs;
    @Column(name = "slack_thread_ts", length = 40) private String slackThreadTs;
    @Column(name = "channel_type", length = 20) private String channelType;
    @Column(columnDefinition = "text") private String text;
    @Column(name = "action_token") private UUID actionToken;
    @Column(name = "founder_comment", columnDefinition = "text") private String founderComment;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private QueueStatus status;
    @Column(name = "attempt_count", nullable = false) private int attemptCount;
    @Column(name = "available_at", nullable = false) private Instant availableAt;
    @Column(name = "processed_at") private Instant processedAt;
    @Column(name = "error_message", columnDefinition = "text") private String errorMessage;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected SlackInboxEvent() {}
    public static SlackInboxEvent message(String externalId, String teamId, String userId, String channelId, String messageTs, String threadTs, String channelType, String text) {
        return new SlackInboxEvent(externalId, "MESSAGE", teamId, userId, channelId, messageTs, threadTs, channelType, text, null, null);
    }
    public static SlackInboxEvent decision(String externalId, String teamId, String userId, String channelId, String messageTs, String threadTs, UUID actionToken, String comment) {
        return new SlackInboxEvent(externalId, "DECISION_ACTION", teamId, userId, channelId, messageTs, threadTs, null, null, actionToken, comment);
    }
    private SlackInboxEvent(String externalId, String eventType, String teamId, String userId, String channelId, String messageTs, String threadTs, String channelType, String text, UUID actionToken, String founderComment) {
        this.id = UUID.randomUUID(); this.externalId = externalId; this.eventType = eventType; this.slackTeamId = teamId; this.slackUserId = userId; this.slackChannelId = channelId; this.slackMessageTs = messageTs; this.slackThreadTs = threadTs; this.channelType = channelType; this.text = text; this.actionToken = actionToken; this.founderComment = founderComment; this.status = QueueStatus.PENDING; this.attemptCount = 0; this.availableAt = Instant.now(); this.createdAt = this.updatedAt = Instant.now();
    }
    public void processing() { status = QueueStatus.PROCESSING; attemptCount++; updatedAt = Instant.now(); }
    public void processed() { status = QueueStatus.PROCESSED; processedAt = Instant.now(); updatedAt = processedAt; errorMessage = null; }
    public void retry(String message) { status = QueueStatus.PENDING; errorMessage = safe(message); availableAt = Instant.now().plusSeconds(Math.min(300, 1L << Math.min(attemptCount, 8))); updatedAt = Instant.now(); }
    public void failed(String message) { status = QueueStatus.FAILED; errorMessage = safe(message); updatedAt = Instant.now(); }
    private String safe(String message) { return message == null ? "Unknown processing failure" : message.substring(0, Math.min(message.length(), 1000)); }
    public UUID getId() { return id; } public String getExternalId() { return externalId; } public String getEventType() { return eventType; } public String getSlackTeamId() { return slackTeamId; } public String getSlackUserId() { return slackUserId; } public String getSlackChannelId() { return slackChannelId; } public String getSlackMessageTs() { return slackMessageTs; } public String getSlackThreadTs() { return slackThreadTs; } public String getChannelType() { return channelType; } public String getText() { return text; } public UUID getActionToken() { return actionToken; } public String getFounderComment() { return founderComment; } public QueueStatus getStatus() { return status; } public int getAttemptCount() { return attemptCount; } public Instant getAvailableAt() { return availableAt; }
}
